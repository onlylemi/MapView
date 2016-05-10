package com.onlylemi.mapview.library.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.onlylemi.mapview.library.MapView;

/**
 * MapLayer
 *
 * @author: onlylemi
 */
public class MapLayer extends MapBaseLayer {

    private static final String TAG = "MapLayer";

    private Picture image;
    private boolean hasMeasured;

    public MapLayer(MapView mapView) {
        super(mapView);
        level = MAP_LEVEL;
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
        mapView.setCurrentZoom(zoom, 0, 0);

        float width = mapView.getWidth() - zoom * image.getWidth();
        float height = mapView.getHeight() - zoom * image.getHeight();

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
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        canvas.save();
        canvas.setMatrix(currentMatrix);
        if (image != null) {
            canvas.drawPicture(image);
        }
        canvas.restore();
    }

    public Picture getImage() {
        return image;
    }
}
