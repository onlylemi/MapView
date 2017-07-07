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

    private Picture image;
    private Bitmap bmp;
    private boolean hasMeasured;
    private MapAABB mapBoundingBox;

    public MapLayer(MapView mapView) {
        super(mapView);
        level = MAP_LEVEL;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public void setImage(Picture image) {
        this.image = image;

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
        float zoom = getInitZoom(mapView.getWidth(), mapView.getHeight(), image.getWidth(), image
                .getHeight());
        Log.i(TAG, Float.toString(zoom));
        mapView.initZoom(zoom, 0, 0);

        float width = mapView.getWidth() - zoom * image.getWidth();
        float height = mapView.getHeight() - zoom * image.getHeight();

        Log.i(TAG, "MapWidth: " + mapView.getWidth());
        Log.i(TAG, "MapHeight: " + mapView.getHeight());

        //Create AABB
        mapBoundingBox = new MapAABB( new PointF(0, 0), this.image.getWidth(), this.image.getHeight());

        mapView.translate(width / 2, height / 2);

        Log.i(TAG, mapBoundingBox.toString());

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
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //canvas.setMatrix(currentMatrix);
        if (image != null) {
            canvas.drawBitmap(bmp, currentMatrix, paint);
            //canvas.drawPicture(image);
            mapBoundingBox.update(currentMatrix);
        }
        canvas.restore();
    }

    public MapAABB getMapBoundingBox() {
        return mapBoundingBox;
    }

    public Picture getImage() {
        return image;
    }
}
