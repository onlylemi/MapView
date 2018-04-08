package com.onlylemi.mapview.library.utils.collision;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 14/12/2017.
 */

public class MapAxisBox extends BaseCollision {

    private float width;
    private float height;


    public MapAxisBox(PointF position, float width, float height) {
        this.position = new PointF(position.x - (width / 2), position.y - (height / 2));
        this.width = width;
        this.height = height;

        this.debugPaint = new Paint();
        this.debugPaint.setStyle(Paint.Style.STROKE);
        this.debugPaint.setStrokeWidth(2.0f);
    }


    @Override
    public boolean isPointInside(PointF position) {
        return ((position.x > this.position.x && position.x < this.position.x + width) &&
                (position.y > this.position.y && position.y < this.position.y + height));
    }

    @Override
    public void debugDraw(Matrix m, Canvas canvas) {
        PointF topLeft = MapMath.transformPoint(m, position);
        PointF botRight = MapMath.transformPoint(m, new PointF(position.x + width, position.y + height));
        canvas.drawRect(topLeft.x, topLeft.y, botRight.x, botRight.y, debugPaint);
    }
}
