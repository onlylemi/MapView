package com.onlylemi.mapview.library.utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by patny on 2017-06-30.
 */

@Deprecated
public class MapAABB {
    public static final String TAG = "MapAABB";

    //Same as the orignalTopLeft position
    private PointF position;

    private final float originalWidth, originalHeight;
    private float actualWidth, actualHeight;

    private PointF orignalTopLeft, orignalTopRight, orignalBotLeft, orignalBotRight;
    private PointF actualTopLeft, actualTopRight, actualBotLeft, actualBotRight;

    private float padding = 0.0f;

    public MapAABB(PointF position, float width, float height) {
        this.originalWidth = width;
        this.originalHeight = height;
        this.position = position;

        //Calculate corners
        orignalTopLeft = new PointF(0, 0);
        orignalTopRight = new PointF(width, 0);
        orignalBotLeft = new PointF(0, height);
        orignalBotRight = new PointF(width, height);

        //We need to set the actual points aswell to handle static boxes
        actualTopLeft = orignalTopLeft;
        actualTopRight = orignalTopRight;
        actualBotLeft = orignalBotLeft;
        actualBotRight = orignalBotRight;

        recalculatedDimensions(actualTopLeft, actualTopRight, actualBotLeft, actualBotRight);

        Log.d(TAG, "Corners calculated, orignalTopLeft: " + orignalTopLeft + "\n orignalTopRight: " + orignalTopRight + "\n orignalBotLeft: " + orignalBotLeft + "\n orignalBotRight: " + orignalBotRight);
        Log.d(TAG, "Hieghts and Widths calculated, actualWidth: " + actualWidth + "\n actualHeight: " + actualHeight);
    }

    /**
     * Returns true if the inpuit AABB is fully instersecting (all 4 corners inside) this one
     * @param box AABB
     * @return true if intersecting
     */
    public boolean isFullyIntersecting(MapAABB box) {
        return  ((box.getOrignalTopLeft().x >= this.getOrignalTopLeft().x) && (box.getOrignalTopLeft().y >= this.getOrignalTopLeft().y)) && //TopLeft corner
                ((box.getOrignalTopRight().x <= this.getOrignalTopRight().x) && (box.getOrignalTopRight().y >= this.getOrignalTopRight().y)) && //TopRight corner
                ((box.getOrignalBotLeft().x >= this.getOrignalBotLeft().x) && (box.getOrignalBotLeft().y <= this.getOrignalBotLeft().y)) && //BotLeft corner
                ((box.getOrignalBotRight().x <= this.getOrignalBotRight().x) && (box.getOrignalBotRight().y <= this.getOrignalBotRight().y)); //BotRight corner
    }

    /**
     * Retrieves a 2D position from matrix and translates our position.
     * Will ignore any rotation
     *
     * @param matrix
     */
    @Deprecated
    public void translate(Matrix matrix) {
        float[] fMatrix = new float[9];
        matrix.getValues(fMatrix);

        float x = fMatrix[2];
        float y = fMatrix[5];

        position.x = x;
        position.y = y;
    }

    /**
     * Updates the AABB by the given model matrix
     * @param m matrix
     */
    public void update(Matrix m) {
        //Copy matrix
        Matrix tmp = new Matrix();
        tmp.set(m);

        //Strip position && update position
        float[] mtrx = new float[9];
        tmp.getValues(mtrx);

        //update pos
        position.x = mtrx[2];
        position.y = mtrx[5];

        //Transform all corners
        actualTopLeft =  MapMath.transformPoint(tmp, orignalTopLeft);
        actualTopRight = MapMath.transformPoint(tmp, orignalTopRight);
        actualBotLeft = MapMath.transformPoint(tmp, orignalBotLeft);
        actualBotRight = MapMath.transformPoint(tmp, orignalBotRight);

        //Recalculate the dimensions using the new world coordinates
        recalculatedDimensions(actualTopLeft, actualTopRight, actualBotLeft, actualBotRight);
    }

    /**
     * Transforms a point using matrix and returns a new point
     * @param point
     * @param m
     */
    private PointF transformPoint(final PointF point,final Matrix m) {
        float[] vector = { point.x, point.y };
        float[] dst = new float[2];

        m.mapPoints(dst, vector);

        return new PointF(dst[0], dst[1]);
    }

    private void recalculatedDimensions(final PointF topLeft,final PointF topRight,final
    PointF botLeft,final PointF botRight) {
        //Caluclate width and height
        actualWidth = Math.abs(topLeft.x - topRight.x); //This is a cheat as we are only scaling and never translating
        actualHeight = Math.abs(topLeft.y - botLeft.y);
    }

    @Override
    public String toString() {
        return "TopLeft: " + orignalTopLeft.toString() + "\nTopRight: " + orignalTopRight.toString() + "\nBotLeft: " + orignalBotLeft.toString() + "\nBotRight: " + orignalBotRight.toString()
                + "Position X : " + position.x + " , Y: " + position.y;
    }

    public PointF getOrignalTopLeft() {
        return actualTopLeft;
    }

    public PointF getOrignalTopRight() {
        return actualTopRight;
    }

    public PointF getOrignalBotLeft() {
        return actualBotLeft;
    }

    public PointF getOrignalBotRight() {
        return actualBotRight;
    }

}

