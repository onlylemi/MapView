package com.onlylemi.mapview.library.graphics;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Created by patny on 2017-08-04.
 * Basic animation class that represents a animation
 */

public interface IBaseAnimation {

    /**
     * Returns true if the animation is finished
     */
    public boolean isDone();

    /**
     * Called before the animation starts getting updates
     */
    public void onInit(BaseGraphics obj);

    /**
     * Will get called by the graphics object doing the animation each frame
     * @param deltaTime time between updates
     * @return True when the animation is considered finished
     */
    public Matrix update(Matrix m, long deltaTime);

    /**
     * Called when the animation is done
     */
    public void onExit();

}
