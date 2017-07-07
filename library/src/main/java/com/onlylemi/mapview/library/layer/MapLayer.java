package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
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
    private boolean hasMeasured;
    private MapAABB mapBoundingBox;
    private Paint paint;

    public MapLayer(MapView mapView) {
        super(mapView);
        level = MAP_LEVEL;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;

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
    private void initMapLayer() {
        float zoom = getInitZoom(mapView.getWidth(), mapView.getHeight(), bmp.getWidth(), bmp
                .getHeight());
        Log.i(TAG, Float.toString(zoom));
        mapView.initZoom(zoom, 0, 0);

        float width = mapView.getWidth() - zoom * bmp.getWidth();
        float height = mapView.getHeight() - zoom * bmp.getHeight();

        paint = new Paint();
        paint.setAntiAlias(true);

        //Create AABB
        mapBoundingBox = new MapAABB( new PointF(0, 0), this.bmp.getWidth(), this.bmp.getHeight());

        mapView.translate(width / 2, height / 2);

        //Update the bounding box once
        mapBoundingBox.update(mapView.getCurrentTransform());
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
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        canvas.save();
        if (bmp != null) {
            canvas.drawBitmap(bmp, currentMatrix, paint);
            mapBoundingBox.update(currentMatrix);
        }
        canvas.restore();
    }

    public MapAABB getMapBoundingBox() {
        return mapBoundingBox;
    }

    public Bitmap getImage() {
        return bmp;
    }
}
