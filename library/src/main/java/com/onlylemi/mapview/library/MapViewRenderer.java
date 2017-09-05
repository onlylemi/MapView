package com.onlylemi.mapview.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onlylemi.mapview.library.graphics.IBackground;
import com.onlylemi.mapview.library.graphics.implementation.Backgrounds.ColorBackground;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
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
    private SurfaceHolder root;
    private MapView mapView;
    private boolean running = false;
    private List<MapBaseLayer> layers;
    private IBackground background;

    private Object pauseLock = new Object();
    private boolean paused = false;
    //region debug

    private boolean debug = false;
    private int frameCounter = 0;
    private long frameTimeAccumilator = 0;
    private int FPS = 0;

    //endregion

    public MapViewRenderer() {}

    public MapViewRenderer(SurfaceHolder root, MapView mapView) {
        init(root, mapView);
    }

    public void init(SurfaceHolder root, MapView mapView) {
        this.root = root;
        this.mapView = mapView;
        //Default background is black
        background = new ColorBackground(Color.BLACK);
        layers = mapView.getLayers();
    }

    @Override
    public void run() {

        frameTimer.start();

        while(running) {

            frameTimer.update();

            //Lock for painting
            Canvas canvas = root.lockCanvas();

            //If the program exits while we are running break
            //Means somthing managed to remove the canvas before we locked it
            if(canvas == null)
                break;

            background.draw(canvas);

            //// TODO: 2017-08-14 This will be a seperate controller later on
            mapView.updateModes(frameTimer.getFrameTimeNano());

            for (MapBaseLayer layer : layers) {
                if (layer.isVisible) {
                    layer.draw(canvas, worldMatrix, zoom, frameTimer.getFrameTimeNano());

                    if (debug) {
                        layer.debugDraw(canvas, worldMatrix);
                    }
                }
            }

            if(debug) {
                drawDebugValues(canvas, frameTimer.getFrameTimeNano());
            }

            root.unlockCanvasAndPost(canvas);

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

        Log.d(TAG, "Exiting run");
    }

    public void pauseExecution() {
        Log.d(TAG, "Paused rendering");
        synchronized (pauseLock) {
            paused = true;
        }
    }

    public void resumeExecution() {
        Log.d(TAG, "Resumed rendering");
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
            frameTimer.start();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
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
            canvas.drawText("Hardware accelerated: " + mapView.isHardwareAccelerated(), 10, 120, p);
        }
    }
}