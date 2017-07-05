package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by patny on 2017-07-05.
 */

public interface IMark {
    /**
     * Called each refresh before drawing
     * @param m transform matrix
     */
    void update(Matrix m);

    /**
     * Called each refresh after updating
     * @param canvas to draw on
     * @param paint object
     */
    void draw(final Canvas canvas, Paint paint);

    /**
     * Called on each touch input. Return true if position is considered hitting this mark
     * @param position touch input position
     * @return true if hit
     */
    boolean hit(PointF position);
}
