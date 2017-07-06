package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by patny on 2017-07-05.
 */

public interface IMark extends IBaseGraphics {
    /**
     * Called on each touch input. Return true if position is considered hitting this mark
     * @param position touch input position
     * @return true if hit
     */
    boolean hit(PointF position);
}
