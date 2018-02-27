package com.onlylemi.mapview.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.onlylemi.mapview.library.graphics.IBackground;
import com.onlylemi.mapview.library.graphics.implementation.Backgrounds.ColorBackground;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.layer.MapLayer;
import com.onlylemi.mapview.library.messages.ICameraModeCommand;
import com.onlylemi.mapview.library.messages.ICommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapRenderTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patny on 2017-08-14.
 */

/**
 * // TODO: 2018-02-17 Document this class properly
 */
public class MapViewRenderer extends Thread {
    private static final String TAG = "MapViewRenderer";

    private Matrix worldMatrix = new Matrix();
    private float zoom = 1.0f;

    private MapRenderTimer frameTimer = new MapRenderTimer();
    private Surface root;
    private SurfaceHolder rootHolder;
    private MapView mapView;
    private boolean running = false;
    private MapLayer rootLayer;
    private List<MapBaseLayer> layers;
    private IBackground background;

    private MapViewCamera camera;

    //We call this once we start running to allow the user to setup the map
    private MapViewSetupCallback setupCallback;
    private Object setupLock = new Object();

    private Object pauseLock = new Object();
    private boolean paused = true;

    private Handler messageHandler;

    private boolean isSetupDone = false;

    //region debug

    private boolean debug = false;
    private int frameCounter = 0;
    private long frameTimeAccumilator = 0;
    private int FPS = 0;
    private int droppedFrames = 0;

    //endregion

    public MapViewRenderer() {}

    public MapViewRenderer(SurfaceHolder root, MapView mapView) {
        init(root, mapView);
    }

    public void init(SurfaceHolder root, MapView mapView) {
        this.root = root.getSurface();
        this.rootHolder = root;
        this.mapView = mapView;
        //Default background is black
        background = new ColorBackground(Color.RED);
        //layers = mapView.getLayers();
        layers = new ArrayList<>();
    }

    public void onSurfaceChanged(SurfaceHolder holder, int width, int height) {
        this.background.onSurfaceChanged(width, height);
    }

    @Override
    public void run() {

        /*
            This locks the render thread until the user sets up the setup callback
         */
        synchronized (setupLock) {
            while(setupCallback == null) {
                try {
                    setupLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(TAG, "Setup callback now set");

        Looper.prepare();

        Log.d(TAG, "Preparing message handler");

        messageHandler = new MapViewMessageHandler();

        //// TODO: 27/12/2017 Maybe rename to setup handler?
        MapViewHandler setupHandler = new MapViewHandler(this.mapView, this);
        setupCallback.onSetup(setupHandler);
        finishSetup(setupHandler.getUser());
        setupCallback.onPostSetup();

        Log.d(TAG, "Setup callback finished");

        Log.d(TAG, "Everything setup, starting looper");

        Looper.loop();

        Log.d(TAG, "Exiting run");
    }

    public Handler getHandler() {
        return messageHandler;
    }

    private Canvas canvas = null;

    private long oldTimeStamp;
    public void doFrame(long timeStamp) {

        if((System.nanoTime() - timeStamp) / 1000000 > 15) {
            return;
        }

        long deltaTimeNano = timeStamp - oldTimeStamp;
        oldTimeStamp = timeStamp;

        //Lock for painting
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            canvas = root.lockHardwareCanvas();
        }else {
            canvas = rootHolder.lockCanvas();
        }

        //If the program exits while we are running break
        //Means somthing managed to remove the canvas before we locked it
        if(canvas == null)
            return;

        background.draw(canvas);
        
        Matrix m = camera.update(deltaTimeNano);

        for (MapBaseLayer layer : layers) {
            if (layer.isVisible) {
                layer.draw(canvas, m, camera.getCurrentZoom(), deltaTimeNano);

                if (debug) {
                    layer.debugDraw(canvas, m);
                }
            }
        }

        if(debug) {
            drawDebugValues(canvas, deltaTimeNano);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            root.unlockCanvasAndPost(canvas);
        }else {
            rootHolder.unlockCanvasAndPost(canvas);
        }


        canvas = null;
    }

    /**
     * Called once the user has done all their map setups. This just inits all the zooming, camera and links everything together
     */
    private void finishSetup(@Nullable LocationUser user) {
        if(rootLayer != null) {
            camera = new MapViewCamera(mapView.getWidth(), mapView.getHeight(), rootLayer.getWidth(), rootLayer.getHeight());
            camera.initialize(user);
            isSetupDone = true;
        } else {
            //// TODO: 26/12/2017 Create my own exception for this!
            throw new RuntimeException("You need to create at least one maplayer");
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if(!running) {
            Looper.myLooper().quit();
        }
    }

    @Deprecated
    public void waitUntilReady() {
        synchronized (pauseLock) {
            while(paused) {
                try {
                    pauseLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * The callback to call when the mapview is ready
     * @param callback
     */
    public void setSetupCallback(MapViewSetupCallback callback) {
        synchronized (setupLock) {
            setupCallback = callback;
            setupLock.notifyAll();
        }
    }

    public void addLayer(MapBaseLayer layer) {
        layers.add(layer);
    }

    public void setMapLayer(MapLayer layer) {
        rootLayer = layer;
    }

    public void setDebug(boolean enableDebug) {
        debug = enableDebug;
    }

    public boolean getDebug() {
        return debug;
    }

    @Deprecated
    public Matrix getWorldMatrix() {
        return worldMatrix;
    }

    @Deprecated
    public void setWorldMatrix(Matrix worldMatrix) {
        this.worldMatrix = worldMatrix;
    }

    @Deprecated
    public float getZoom() {
        return zoom;
    }

    @Deprecated
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    //@Deprecated?
    public boolean isSetupDone()
    {
        return isSetupDone;
    }

    @Deprecated
    public IBackground getBackground() {
        return background;
    }

    @Deprecated
    public void setBackground(IBackground background) {
        this.background = background;
    }

    /**
     * Draws the current FPS on the screen
     * @param canvas
     * @param deltaTimeNano
     */
    private void drawDebugValues(Canvas canvas, long deltaTimeNano) {
        frameTimeAccumilator += deltaTimeNano;
        frameCounter++;

        if(frameTimeAccumilator >= MapMath.NANOSECOND) {
            FPS = (int) (MapMath.NANOSECOND / (frameTimeAccumilator / frameCounter));
            frameTimeAccumilator = 0;
            frameCounter = 0;
        }

        if(FPS > 0) {
            Paint p = new Paint();
            p.setTextSize(25);
            p.setColor(Color.YELLOW);
            canvas.drawText("FPS: " + FPS, 10, 80, p);
            canvas.drawText("Hardware accelerated: " + canvas.isHardwareAccelerated(), 10, 120, p);
        }
    }

    /**
     * Any external write/update calls to the render thread MUST go through the handler
     */
    private class MapViewMessageHandler extends Handler{

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case MessageDefenitions.MESSAGE_DRAW:
                    doFrame((((long) msg.arg1) << 32) |
                            (((long) msg.arg2) & 0xffffffffL));
                    break;
                case MessageDefenitions.MESSAGE_CAMERA_MODE_EXECUTE:
                    ((ICameraModeCommand) msg.obj).execute(camera);
                    break;
                case MessageDefenitions.MESSAGE_EXECUTE:
                    ((ICommand) msg.obj).execute();
                    break;
                default:
                    if(msg.what >= MessageDefenitions.MESSAGE_MOTIONEVENT_LOWER_RANGE &&
                            msg.what <= MessageDefenitions.MESSAGE_MOTIONEVENT_UPPER_RANGE) {
                        switch(msg.what) {
                            case MotionEvent.ACTION_POINTER_DOWN:
                                feedInputToCamera(msg.what, new PointF(msg.arg1, msg.arg2), (int) msg.obj);
                                break;
                            case MotionEvent.ACTION_UP:
                                feedInputToCamera(msg.what, new PointF(msg.arg1, msg.arg2));

                                for (MapBaseLayer layer : layers) {
                                    layer.onTouch(new PointF(msg.arg1, msg.arg2));
                                }
                                break;
                            default:
                                feedInputToCamera(msg.what, new PointF(msg.arg1, msg.arg2));
                        }
                    }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * Sends input events to the camera
     * @param inputAction
     * @param point
     * @param extras any extras information (ex. pointer count). null if nothing
     */
    private void feedInputToCamera(int inputAction, PointF point, int extras) {
        camera.handleInput(inputAction, point, extras);
    }

    private void feedInputToCamera(int inputAction, PointF point) {
        camera.handleInput(inputAction, point, -1);
    }

    public void handleInput(int action, MotionEvent event) {
        switch(action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                feedInputToCamera(action, new PointF(event.getX(), event.getY()), event.getPointerCount());
                break;
            case MotionEvent.ACTION_UP:
                feedInputToCamera(action, new PointF(event.getX(), event.getY()));

                for (MapBaseLayer layer : layers) {
                    layer.onTouch(new PointF(event.getX(), event.getY()));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                feedInputToCamera(action, new PointF(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_DOWN:
                feedInputToCamera(action, new PointF(event.getX(), event.getY()));
                break;
        }
    }
}