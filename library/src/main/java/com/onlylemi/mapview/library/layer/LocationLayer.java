package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.R;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

/**
 * LocationLayer
 *
 * @author: onlylemi
 */
public class LocationLayer extends MapBaseLayer {

    //user
    private LocationUser user;

    private Paint locationPaint;

    private PointF currentPosition = null;

    public LocationLayer(MapView mapView, LocationUser user) {
        super(mapView);
        this.user = user;
        initLayer();
    }

    private void initLayer() {
        locationPaint = new Paint();
        locationPaint.setAntiAlias(true);
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        //Later I wanna handle movement directions and shit in this layer
        if (isVisible) {
            canvas.save();
            user.update(currentMatrix);
            user.draw(canvas, locationPaint);
            canvas.restore();
        }
    }

    public PointF getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PointF currentPosition) {
        this.currentPosition = currentPosition;
    }
}
