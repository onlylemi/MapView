package com.onlylemi.mapview.library;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Region;
import android.util.Log;

import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 26/12/2017.
 */
//I'ave kept this class seperate because I wanna add level support in the future
public class MapViewCamera {
    private static final String TAG = "MapViewCamera";

    private Matrix worldMatrix = new Matrix();
    private float currentZoom = 1; //Its just alot easier to keep track of any scaling like this
    private PointF currentPosition = new PointF();

    //MapView width and height
    private int viewWidth;
    private int viewHeight;

    //The actual current maplayer width and height
    private int mapWidth;
    private int mapHeight;

    //These are zoom paddings - Multiples, aka 2 = you can zoom in twice the size of the original
    private float maxZoomPadding = 2.0f;
    private float minZoomPadding = 0.5f;

    private float maxZoom;
    private float minZoom;

    public MapViewCamera(int viewWidth, int viewHeight, int mapWidth, int mapHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        initZoom();
    }

    /**
     * Will initialize/create all modes
     */
    public void initialize() {

    }

    /**
     * Updates any camera mode and also returns the world matrix
     * @param deltaTimeNano
     * @return
     */
    public Matrix update(long deltaTimeNano) {
        return worldMatrix;
    }

    /**
     * Calculates the min and max zoom values
     * We assume the default starting mode is the entire map in view centered
     */
    public void initZoom() {
        float widthRatio = (float) viewWidth / mapWidth;
        float heightRatio = (float) viewHeight / mapHeight;

        Log.i(TAG, "widthRatio:" + widthRatio);
        Log.i(TAG, "heightRatio:" + heightRatio);

        float zoom = 1.0f;

        if (widthRatio * mapHeight <= viewHeight) {
            zoom = widthRatio;
        } else if (heightRatio * mapWidth <= viewWidth) {
            zoom = heightRatio;
        }

        minZoom = currentZoom - (currentZoom * minZoomPadding);
        //If set to use contain user mode, this value will get overridden to prevent jerking
        maxZoom = currentZoom * maxZoomPadding;

        zoom(zoom, 0, 0);
        translate((viewWidth / 2) - ((mapWidth * currentZoom) / 2), (viewHeight / 2) - ((mapHeight * currentZoom) / 2));
    }

    public void translate(float x, float y) {
        currentPosition.x += x;
        currentPosition.y += y;
        worldMatrix.postTranslate(x, y);
    }

    public void zoom(float zoom) {
        zoom(zoom, viewWidth / 2, viewHeight / 2);
    }

    public void zoom(float zoom, float worldX, float worldY) {
        //float newZoom = MapMath.truncateNumber(zoom, minZoom, maxZoom);
        worldMatrix.postScale(zoom / currentZoom, zoom / currentZoom, worldX, worldY);
        currentZoom = zoom;
    }

    //region GETSET

    public float getCurrentZoom() {
        return currentZoom;
    }

    public PointF getCurrentPosition() {
        return currentPosition;
    }

    //endregion GETSET

}
