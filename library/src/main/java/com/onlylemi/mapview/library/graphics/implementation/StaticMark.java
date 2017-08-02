package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.onlylemi.mapview.library.graphics.*;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-08-01.
 */

public class StaticMark extends com.onlylemi.mapview.library.graphics.BaseMark {
    public final static String TAG = "BaseMark";

    //Image
    protected Bitmap bmp;

    //Position
    protected PointF position;

    protected PointF worldPosition;

    public StaticMark(Bitmap bmp, PointF position) {
        this.bmp = bmp;
        this.position = new PointF(position.x, position.y);

        //Calculate radius
        clickRadius = bmp.getWidth() > bmp.getHeight() ? bmp.getWidth() / 2 : bmp.getHeight() / 2;
    }

    public void update(Matrix m) {
        worldPosition = MapMath.transformPoint(m, position);

        tMatrix = new Matrix();
        tMatrix.postTranslate(position.x - bmp.getWidth() / 2, position.y - bmp.getHeight() / 2);

        //Dont use a model matrix atm so, fix this later
        tMatrix.setValues(MapMath.matrixMultiplication(m, tMatrix));
    }

    public void draw(final Canvas canvas, Paint paint) {
        canvas.drawBitmap(bmp, tMatrix, paint);

        canvas.drawCircle(worldPosition.x, worldPosition.y, clickRadius, paint);
    }

    public boolean hit(PointF position) {
        //Must add half width and half height to position otherwise we are calculating from the topleft corner
        return MapMath.getDistanceBetweenTwoPoints(this.position, position) <= clickRadius;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getWorldPosition() {
        return worldPosition;
    }

    public void setWorldPosition(PointF worldPosition) {
        this.worldPosition = worldPosition;
    }

    public float getRadius() {
        return clickRadius;
    }

    public void setRadius(float radius) {
        this.clickRadius = clickRadius;
    }
}
