package com.onlylemi.mapview.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.onlylemi.mapview.library.graphics.IBackground;
import com.onlylemi.mapview.library.graphics.implementation.Backgrounds.ColorBackground;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.messages.ICommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapRenderTimer;

import java.util.List;
import java.util.Objects;

/**
 * Created by patny on 2017-08-14.
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
    private List<MapBaseLayer> layers;
    private IBackground background;

    private Object pauseLock = new Object();
    private boolean paused = true;

    private Handler messageHandler;

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
        background = new ColorBackground(Color.BLACK);
        layers = mapView.getLayers();
    }

    public void onSurfaceChanged(SurfaceHolder holder, int width, int height) {
        this.background.onSurfaceChanged(width, height);
    }

    @Override
    public void run() {
        Looper.prepare();

        messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MessageDefenitions.MESSAGE_DRAW) {
                    doFrame((((long) msg.arg1) << 32) |
                            (((long) msg.arg2) & 0xffffffffL));
                } else if(msg.what == MessageDefenitions.MESSAGE_EXECUTE) {
                    ((ICommand) msg.obj).execute();
                } else if(msg.what == 0) {
                    setRunning(false);
                }

                //msg.recycle();
            }
        };

        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }

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

        //// TODO: 2017-08-14 This will be a seperate controller later on
        mapView.updateModes(deltaTimeNano);

        for (MapBaseLayer layer : layers) {
            if (layer.isVisible) {
                layer.draw(canvas, worldMatrix, zoom, deltaTimeNano);

                if (debug) {
                    layer.debugDraw(canvas, worldMatrix);
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

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if(!running) {
            Looper.myLooper().quit();
        }
    }

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

    public void setDebug(boolean enableDebug) {
        debug = enableDebug;
    }

    public boolean getDebug() {
        return debug;
    }

    public Matrix getWorldMatrix() {
        return worldMatrix;
    }

    public void setWorldMatrix(Matrix worldMatrix) {
        this.worldMatrix = worldMatrix;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public IBackground getBackground() {
        return background;
    }

    public void setBackground(IBackground background) {
        this.background = background;
    }

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
}