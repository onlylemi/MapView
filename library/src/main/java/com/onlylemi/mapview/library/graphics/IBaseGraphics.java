package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by patny on 2017-07-05.
 */

public interface IBaseGraphics {
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
}
