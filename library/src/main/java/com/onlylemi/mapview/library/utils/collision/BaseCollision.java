package com.onlylemi.mapview.library.utils.collision;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by patnym on 14/12/2017.
 */

public abstract class BaseCollision {

    protected PointF position;

    protected Paint debugPaint;


    public abstract boolean isPointInside(PointF position);

    public abstract void debugDraw(final Matrix m, final Canvas canvas);

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }
}
