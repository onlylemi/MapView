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
    protected RectF dimensions;
    protected boolean hasMeasured;
    //Deprecated
    //private MapAABB mapBoundingBox;
    protected Paint paint;

    public MapLayer(MapView mapView) {
        super(mapView);
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
        dimensions = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
        if (mapView.getWidth() == 0) {
            ViewTreeObserver vto = mapView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (!hasMeasured) {
                        initMapLayer();
                        hasMeasured = true;
                    }
                    return true;
                }
            });
        } else {
            initMapLayer();
        }
    }

    /**
     * init map image layer
     */
    public void initMapLayer() {
        float zoom = getInitZoom(mapView.getWidth(), mapView.getHeight(), dimensions.width(), dimensions.height());
        Log.i(TAG, Float.toString(zoom));
        mapView.initZoom(zoom, 0, 0);

        float width = mapView.getWidth() - zoom * dimensions.width();
        float height = mapView.getHeight() - zoom * dimensions.height();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        mapView.translate(width / 2, height / 2);
    }

    /**
     * calculate init zoom
     *
     * @param viewWidth
     * @param viewHeight
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    private float getInitZoom(float viewWidth, float viewHeight, float imageWidth,
                              float imageHeight) {
        float widthRatio = viewWidth / imageWidth;
        float heightRatio = viewHeight / imageHeight;

        Log.i(TAG, "widthRatio:" + widthRatio);
        Log.i(TAG, "widthRatio:" + heightRatio);

        if (widthRatio * imageHeight <= viewHeight) {
            return widthRatio;
        } else if (heightRatio * imageWidth <= viewWidth) {
            return heightRatio;
        }
        return 0;
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {
        if (bmp != null) {
            canvas.drawBitmap(bmp, currentMatrix, paint);
        }
    }

    @Override
    public void debugDraw(Canvas canvas, Matrix currentMatrix) {

    }

    public RectF getDimensions() {
        return dimensions;
    }

    public Bitmap getImage() {
        return bmp;
    }
}
