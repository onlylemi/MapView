package com.onlylemi.mapview.library.graphics;

import android.graphics.PointF;

/**
 * Created by patny on 2017-08-01.
 */

public abstract class BaseMark extends BaseGraphics {

    public float clickRadius;

    /**
     * Called if this graphics object get clicked
     * @return
     */
    public abstract boolean hit(PointF position);
}
