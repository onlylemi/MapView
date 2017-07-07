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
import com.onlylemi.mapview.library.graphics.ILocationUser;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

/**
 * LocationLayer
 *
 * @author: onlylemi
 */
public class LocationLayer extends MapBaseLayer {

    private boolean openCompass = false;

    //user
    private ILocationUser user;

    // compass color
    private static final int DEFAULT_LOCATION_COLOR = 0xFF3EBFC9;
    private static final int DEFAULT_LOCATION_SHADOW_COLOR = 0xFF909090;
    private static final int DEFAULT_INDICATOR_ARC_COLOR = 0xFFFA4A8D;
    private static final int DEFAULT_INDICATOR_CIRCLE_COLOR = 0xFF00F0FF;
    private static final float COMPASS_DELTA_ANGLE = 5.0f;
    private float defaultLocationCircleRadius;

    private float compassLineLength;
    private float compassLineWidth;
    private float compassLocationCircleRadius;
    private float compassRadius;
    private float compassArcWidth;
    private float compassIndicatorCircleRadius;
    private float compassIndicatorGap;
    private float compassIndicatorArrowRotateDegree;
    private float compassIndicatorCircleRotateDegree = 0;
    private Bitmap compassIndicatorArrowBitmap;

    private BitmapLayer compassBitmapLayer;

    private Paint compassLinePaint;
    private Paint locationPaint;
    private Paint indicatorCirclePaint;
    private Paint indicatorArcPaint;

    private PointF currentPosition = null;

    public LocationLayer(MapView mapView, PointF currentPosition) {
        this(mapView, currentPosition, false);
    }

    public LocationLayer(MapView mapView, ILocationUser user) {
        this(mapView, null, false);

        this.user = user;
    }

    public LocationLayer(MapView mapView, PointF currentPosition, boolean openCompass) {
        super(mapView);
        this.currentPosition = currentPosition;
        this.openCompass = openCompass;

        level = LOCATION_LEVEL;
        initLayer();
    }

    private void initLayer() {
        // default locationPaint
        locationPaint = new Paint();
        locationPaint.setAntiAlias(true);
  //      locationPaint.setStyle(Paint.Style.FILL);
 //       locationPaint.setColor(DEFAULT_LOCATION_COLOR);
 //       locationPaint.setShadowLayer(5, 3, 3, DEFAULT_LOCATION_SHADOW_COLOR);
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        if (isVisible) {
            canvas.save();
            user.update(currentMatrix);
            user.draw(canvas, locationPaint);
            canvas.restore();
        }
    }

    public boolean isOpenCompass() {
        return openCompass;
    }

    public void setOpenCompass(boolean openCompass) {
        this.openCompass = openCompass;
    }

    public PointF getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PointF currentPosition) {
        this.currentPosition = currentPosition;
    }

    public float getCompassIndicatorCircleRotateDegree() {
        return compassIndicatorCircleRotateDegree;
    }

    public void setCompassIndicatorCircleRotateDegree(float compassIndicatorCircleRotateDegree) {
        this.compassIndicatorCircleRotateDegree = compassIndicatorCircleRotateDegree;
    }

    public float getCompassIndicatorArrowRotateDegree() {
        return compassIndicatorArrowRotateDegree;
    }

    public void setCompassIndicatorArrowRotateDegree(float compassIndicatorArrowRotateDegree) {
        this.compassIndicatorArrowRotateDegree = compassIndicatorArrowRotateDegree;
    }
}
