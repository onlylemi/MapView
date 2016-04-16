package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;

/**
 * BitmapLayer
 *
 * @author: onlylemi
 */
public class BitmapLayer extends MapBaseLayer {

    private PointF location;
    private Bitmap bitmap;
    private Paint paint;

    private boolean autoScale = false;

    public BitmapLayer(MapView mapView, Bitmap bitmap) {
        this(mapView, bitmap, null);
    }

    public BitmapLayer(MapView mapView, Bitmap bitmap, PointF location) {
        super(mapView);
        this.location = location;
        this.bitmap = bitmap;

        paint = new Paint();
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        if (isVisible && bitmap != null) {
            canvas.save();
            float goal[] = {location.x, location.y};
            if (!autoScale) {
                currentMatrix.mapPoints(goal);
            } else {
                canvas.setMatrix(currentMatrix);
            }
            canvas.drawBitmap(bitmap, goal[0] - bitmap.getWidth() / 2, goal[1]
                    - bitmap.getHeight(), paint);
            canvas.restore();
        }
    }

    public PointF getLocation() {
        return location;
    }

    public void setLocation(PointF location) {
        this.location = location;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
    }

    public boolean isAutoScale() {
        return autoScale;
    }
}
