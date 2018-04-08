package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.utils.MapAABB;

/**
 * MapLayer
 *
 * @author: onlylemi
 */
public class MapLayer extends MapBaseLayer {

    private static final String TAG = "MapLayer";

    private Bitmap bmp;

    private int width;
    private int height;

    protected boolean hasMeasured;
    protected Paint paint;

    public MapLayer(MapView mapView) {
        super(mapView);
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
    }

    @Override
    public void onTouch(float x, float y)
    {

    }

    @Override
    public boolean update(Matrix currentMatrix, long deltaTime) {
        return hasChanged;
    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {
        canvas.drawBitmap(bmp, currentMatrix, paint);
    }

    @Override
    public void debugDraw(Canvas canvas, Matrix currentMatrix) {

    }

    public Bitmap getImage() {
        return bmp;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
