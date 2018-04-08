package com.onlylemi.mapview.library.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
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

    protected boolean hasChanged = false;

    protected MapViewRenderer renderer;

    protected MapView mapView;

    public MapBaseLayer(MapView mapView) {
        this.mapView = mapView;
    }

    public abstract void onTouch(float x, float y);

    public abstract boolean update(Matrix currentMatrix, long deltaTime);
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

    public void createHandler(MapViewRenderer renderThread) {
        this.renderer = renderThread;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    protected float setValue(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, mapView.getResources()
                .getDisplayMetrics());
    }

    public void triggerChange() {
        hasChanged = true;
    }
}
