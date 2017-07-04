package com.onlylemi.mapview.library.utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by patny on 2017-06-30.
 */

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
//        Log.d(TAG, "Comparing input x: " + box.getOrignalTopLeft().x + " is bigger or equals to my x: " + this.getOrignalTopLeft().x + " it equals: " + (box.getOrignalTopLeft().x >= this.getOrignalTopLeft().x) );
//        Log.d(TAG, "Comparing input y: " + box.getOrignalTopLeft().y + " is bigger or equals to my y: " + this.getOrignalTopLeft().y + " it equals: " + (box.getOrignalTopLeft().y >= this.getOrignalTopLeft().y) );
//
//        Log.d(TAG, "Comparing input x: " + box.getOrignalTopRight().x + " is bigger or equals to my x: " + this.getOrignalTopRight().x + " it equals: " + (box.getOrignalTopRight().x <= this.getOrignalTopRight().x) );
//        Log.d(TAG, "Comparing input y: " + box.getOrignalTopRight().y + " is bigger or equals to my y: " + this.getOrignalTopRight().y + " it equals: " + (box.getOrignalTopRight().y >= this.getOrignalTopRight().y) );
//
//        Log.d(TAG, "Comparing input x: " + box.getOrignalBotLeft().x + " is bigger or equals to my x: " + this.getOrignalBotLeft().x + " it equals: " + (box.getOrignalBotLeft().x >= this.getOrignalBotLeft().x));
//        Log.d(TAG, "Comparing input y: " + box.getOrignalBotLeft().y + " is bigger or equals to my y: " + this.getOrignalBotLeft().y + " it equals: " + (box.getOrignalBotLeft().y <= this.getOrignalBotLeft().y));
//
//        Log.d(TAG, "Comparing input x: " + box.getOrignalBotRight().x + " is bigger or equals to my x: " + this.getOrignalBotRight().x + " it equals: " + (box.getOrignalBotRight().x <= this.getOrignalBotRight().x) );
//        Log.d(TAG, "Comparing input y: " + box.getOrignalBotRight().y + " is bigger or equals to my y: " + this.getOrignalBotRight().y + " it equals: " + (box.getOrignalBotRight().y <= this.getOrignalBotRight().y) );

        Log.d(TAG, "In total it equals " + (((box.getOrignalTopLeft().x >= this.getOrignalTopLeft().x) && (box.getOrignalTopLeft().y >= this.getOrignalTopLeft().y)) && //TopLeft corner
                ((box.getOrignalTopRight().x <= this.getOrignalTopRight().x) && (box.getOrignalTopRight().y >= this.getOrignalTopRight().y)) && //TopRight corner
                ((box.getOrignalBotLeft().x >= this.getOrignalBotLeft().x) && (box.getOrignalBotLeft().y <= this.getOrignalBotLeft().y)) && //BotLeft corner
                ((box.getOrignalBotRight().x <= this.getOrignalBotRight().x) && (box.getOrignalBotRight().y <= this.getOrignalBotRight().y))));

        return  ((box.getOrignalTopLeft().x >= this.getOrignalTopLeft().x) && (box.getOrignalTopLeft().y >= this.getOrignalTopLeft().y)) && //TopLeft corner
                ((box.getOrignalTopRight().x <= this.getOrignalTopRight().x) && (box.getOrignalTopRight().y >= this.getOrignalTopRight().y)) && //TopRight corner
                ((box.getOrignalBotLeft().x >= this.getOrignalBotLeft().x) && (box.getOrignalBotLeft().y <= this.getOrignalBotLeft().y)) && //BotLeft corner
                ((box.getOrignalBotRight().x <= this.getOrignalBotRight().x) && (box.getOrignalBotRight().y <= this.getOrignalBotRight().y)); //BotRight corner
    }

    /**
     * Retrieves a 2D position from matrix and translates our position.
     * Will ignore any rotation
     * @param matrix
     */
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

        Log.d(TAG, tmp.toString());

        //Transform all corners
        actualTopLeft =  transformPoint(orignalTopLeft, tmp);
        actualTopRight = transformPoint(orignalTopRight, tmp);
        actualBotLeft = transformPoint(orignalBotLeft, tmp);
        actualBotRight = transformPoint(orignalBotRight, tmp);

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

        Log.d(TAG, "Hieghts and Widths calculated, actualWidth: " + actualWidth + "\n actualHeight: " + actualHeight);
    }

    @Override
    public String toString() {
        return "TopLeft: " + orignalTopLeft.toString() + "\nTopRight: " + orignalTopRight.toString() + "\nBotLeft: " + orignalBotLeft.toString() + "\nBotRight: " + orignalBotRight.toString()
                + "Position X : " + position.x + " , Y: " + position.y;
    }

    /*** GETTERS AND SETTERS ***/
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

    /*** HELPERS ***/
    //// TODO: 2017-06-30 : THIS IS TEMPORARY WHILE THE CALCULATIOSN ARE DONE IN THE WRONG ORDER
    private PointF translatePoint(PointF point) {
        PointF rPoint = new PointF(point.x, point.y);
        //Translate pos
        rPoint.x += position.x;
        rPoint.y += position.y;

        return rPoint;
    }

}

