package com.onlylemi.mapview.library.utils.collision;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 14/12/2017.
 */

public class MapAxisCircle extends BaseCollision {

    private float radius;

    public MapAxisCircle(PointF position, float radius) {
        this.position = position;
        this.radius = radius;

        this.debugPaint = new Paint();
        this.debugPaint.setStyle(Paint.Style.STROKE);
        this.debugPaint.setStrokeWidth(2.0f);
    }

    @Override
    public boolean isPointInside(PointF position) {
        return MapMath.getDistanceBetweenTwoPoints(this.position, position) < this.radius;
    }

    @Override
    public void debugDraw(Matrix m, Canvas canvas) {
        PointF mid = MapMath.transformPoint(m, this.position);
        float tRadius = m.mapRadius(this.radius);
        canvas.drawCircle(mid.x, mid.y, tRadius, debugPaint);
    }
}
