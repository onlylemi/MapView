package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.List;

/**
 * Created by patny on 2017-08-01.
 */

public abstract class BaseGraphics {

    /**
     * Respresents if this graphic is currently rendering
     */
    protected  boolean isVisible = true;

    /**
     * Represents the position of this graphic
     */
    public PointF position;

    /**
     * Represents the rotation of this graphic
     */
    public float rotation = 0.0f;

    /**
     * Represents the model matrix of this graphic
     */
    protected Matrix mMatrix;

    /**
     * Represents the transformed matrix used to render
     */
    protected Matrix tMatrix;

    /**
     * Called each refresh before drawing
     * @param m transform matrix
     */
    public abstract boolean update(final Matrix m, long deltaTime);

    /**
     * Called each refresh after updating
     * @param canvas to draw on
     * @param paint object
     */
    public abstract void draw(final Canvas canvas, final Paint paint);

    /**
     * Called if debug draw is enabled
     */
    public abstract void debugDraw(final Matrix m, final Canvas canvas);


    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean getVisible() {
        return isVisible;
    }
}
