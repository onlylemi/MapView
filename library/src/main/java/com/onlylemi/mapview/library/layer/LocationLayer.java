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

/**
 * LocationLayer
 *
 * @author: onlylemi
 */
public class LocationLayer extends MapBaseLayer {

    private boolean openCompass = false;

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

    public LocationLayer(MapView mapView) {
        this(mapView, null);
    }

    public LocationLayer(MapView mapView, PointF currentPosition) {
        this(mapView, currentPosition, false);
    }

    public LocationLayer(MapView mapView, PointF currentPosition, boolean openCompass) {
        super(mapView);
        this.currentPosition = currentPosition;
        this.openCompass = openCompass;

        level = LOCATION_LEVEL;
        initLayer();
    }

    private void initLayer() {
        // setting dufault values
        defaultLocationCircleRadius = setValue(8f);

        // default locationPaint
        locationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        locationPaint.setAntiAlias(true);
        locationPaint.setStyle(Paint.Style.FILL);
        locationPaint.setColor(DEFAULT_LOCATION_COLOR);
        locationPaint.setShadowLayer(5, 3, 3, DEFAULT_LOCATION_SHADOW_COLOR);


        compassRadius = setValue(38f);
        compassLocationCircleRadius = setValue(0.5f);
        compassLineWidth = setValue(1.3f);
        compassLineLength = setValue(2.3f);
        compassArcWidth = setValue(4.0f);
        compassIndicatorCircleRadius = setValue(2.6f);
        compassIndicatorGap = setValue(15.0f);

        // default compassLinePaint
        compassLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        compassLinePaint.setAntiAlias(true);
        compassLinePaint.setStrokeWidth(compassLineWidth);
        // default indicatorCirclePaint
        indicatorCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorCirclePaint.setAntiAlias(true);
        indicatorCirclePaint.setStyle(Paint.Style.FILL);
        indicatorCirclePaint.setShadowLayer(3, 1, 1, DEFAULT_LOCATION_SHADOW_COLOR);
        indicatorCirclePaint.setColor(DEFAULT_INDICATOR_CIRCLE_COLOR);
        // default indicatorArcPaint
        indicatorArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorArcPaint.setStyle(Paint.Style.STROKE);
        indicatorArcPaint.setColor(DEFAULT_INDICATOR_ARC_COLOR);
        indicatorArcPaint.setStrokeWidth(compassArcWidth);

        compassIndicatorArrowBitmap = BitmapFactory.decodeResource(mapView.getResources(),
                R.mipmap.compass);
        compassBitmapLayer = new BitmapLayer(mapView, compassIndicatorArrowBitmap, null);
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        if (isVisible && currentPosition != null) {
            canvas.save();
            float[] goal = {currentPosition.x, currentPosition.y};
            currentMatrix.mapPoints(goal);

            canvas.drawCircle(goal[0], goal[1], defaultLocationCircleRadius,
                    locationPaint);

            canvas.drawCircle(goal[0], goal[1], defaultLocationCircleRadius,
                    locationPaint);

            if (openCompass) {
                for (int i = 0; i < 360 / COMPASS_DELTA_ANGLE; i++) {
                    canvas.save();
                    canvas.rotate(COMPASS_DELTA_ANGLE * i, goal[0], goal[1]);
                    if (i % (90 / COMPASS_DELTA_ANGLE) == 0) {
                        canvas.drawLine(goal[0], goal[1] - compassRadius
                                + compassLocationCircleRadius, goal[0], goal[1]
                                - compassRadius + compassLocationCircleRadius
                                - compassLineLength, compassLinePaint);
                    } else {
                        canvas.drawCircle(goal[0], goal[1] - compassRadius,
                                compassLocationCircleRadius, new Paint());
                    }
                    canvas.restore();
                }
                if (compassIndicatorArrowBitmap != null) {
                    canvas.save();
                    canvas.rotate(this.compassIndicatorArrowRotateDegree,
                            goal[0], goal[1]);
                    canvas.drawBitmap(compassIndicatorArrowBitmap, goal[0]
                                    - compassIndicatorArrowBitmap.getWidth() / 2,
                            goal[1] - defaultLocationCircleRadius
                                    - compassIndicatorGap, new Paint());
//                    compassBitmapLayer.setLocation(currentPosition);
//                    compassBitmapLayer.draw(canvas, currentMatrix, currentZoom,
//                            currentRotateDegrees);
                    canvas.restore();
                    if (360 - (this.compassIndicatorArrowRotateDegree - this
                            .compassIndicatorCircleRotateDegree) > 180) {
                        canvas.drawArc(
                                new RectF(goal[0] - compassRadius, goal[1]
                                        - compassRadius, goal[0]
                                        + compassRadius, goal[1]
                                        + compassRadius),
                                -90 + this.compassIndicatorCircleRotateDegree,
                                (this.compassIndicatorArrowRotateDegree - this
                                        .compassIndicatorCircleRotateDegree),
                                false, indicatorArcPaint);
                    } else {
                        canvas.drawArc(
                                new RectF(goal[0] - compassRadius, goal[1]
                                        - compassRadius, goal[0]
                                        + compassRadius, goal[1]
                                        + compassRadius),
                                -90 + this.compassIndicatorArrowRotateDegree,
                                360 - (this.compassIndicatorArrowRotateDegree - this
                                        .compassIndicatorCircleRotateDegree),
                                false, indicatorArcPaint);
                    }

                }
                canvas.save();
                canvas.rotate(compassIndicatorCircleRotateDegree, goal[0],
                        goal[1]);
                canvas.drawCircle(goal[0], goal[1] - compassRadius,
                        compassIndicatorCircleRadius, indicatorCirclePaint);
                canvas.restore();
            }
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
