package com.onlylemi.mapview.library;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.SurfaceHolder;

import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.utils.MapRenderTimer;

import java.util.List;

/**
 * Created by patny on 2017-08-14.
 */

public class MapViewRenderer extends Thread {

    private Matrix worldMatrix = new Matrix();
    private float zoom = 1.0f;

    private MapRenderTimer frameTimer = new MapRenderTimer();
    private SurfaceHolder root;
    private MapView mapView;
    private boolean running = false;
    private List<MapBaseLayer> layers;

    //region debug

    private boolean debug = false;

    //endregion

    public MapViewRenderer() {}

    public MapViewRenderer(SurfaceHolder root, MapView mapView) {
        init(root, mapView);
    }

    public void init(SurfaceHolder root, MapView mapView) {
        this.root = root;
        this.mapView = mapView;

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

            //Background color
            canvas.drawColor(mapView.getCanvasBackgroundColor());

            //Update the different map states
            //// TODO: 2017-08-14 This will be a seperate controller later on
            mapView.updateModes(frameTimer.getFrameTimeNano());

            for (MapBaseLayer layer : layers) {
                if (layer.isVisible) {
                    layer.draw(canvas, worldMatrix, zoom, frameTimer.getFrameTimeNano());

                    if (debug)
                        layer.debugDraw(canvas, worldMatrix);
                }
            }
            root.unlockCanvasAndPost(canvas);
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
}