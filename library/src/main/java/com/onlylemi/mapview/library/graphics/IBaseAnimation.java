package com.onlylemi.mapview.library.graphics;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Created by patny on 2017-08-04.
 * Basic animation interface that represents a animation
 */

public interface IBaseAnimation {

    /**
     * Returns true if the animation is finished
     */
    public boolean isDone();

    /**
     * Will get called by the graphics object doing the animation each frame
     * @param deltaTime time between updates
     * @return True when the animation is considered finished
     */
    public Matrix update(Matrix m, long deltaTime);

}
