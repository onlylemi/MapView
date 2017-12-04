package com.onlylemi.mapview.library.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewRenderer;

/**
 * MapBaseLayer
 *
 * @author: onlylemi
 */
public abstract class MapBaseLayer {

    // map layer level
    protected static final int MAP_LEVEL = 0;
    // location layer level
    protected static final int LOCATION_LEVEL = Integer.MAX_VALUE;

    // layer show level
    public int level;
    // layer is/not show
    public boolean isVisible = true;

    protected MapView mapView;

    public MapBaseLayer(MapView mapView) {
        this.mapView = mapView;
    }

    /**
     * touch event
     *
     * @param event
     */
    public abstract void onTouch(MotionEvent event);

    /**
     * draw event
     *
     * @param canvas
     * @param currentMatrix
     * @param currentZoom
     * @param deltaTime
     */
    public abstract void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime);

    public abstract void debugDraw(Canvas canvas, Matrix currentMatrix);

    //todo: This should be abstract later, but not now during development
    public void createHandler(MapViewRenderer renderThread) {

    }

    public void setLevel(int level) {
        this.level = level;
    }

    protected float setValue(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, mapView.getResources()
                .getDisplayMetrics());
    }
}
