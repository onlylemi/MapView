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
        locationPaint.setFilterBitmap(true);
        locationPaint.setDither(true);
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {
        //Later I wanna handle movement directions and shit in this layer
        if (isVisible) {
            user.update(currentMatrix, deltaTime);
            user.draw(canvas, locationPaint);
        }
    }

    @Override
    public void debugDraw(Canvas canvas, Matrix currentMatrix) {
        if(isVisible) {
            user.debugDraw(currentMatrix, canvas);
        }
    }

    public PointF getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PointF currentPosition) {
        this.currentPosition = currentPosition;
    }
}
