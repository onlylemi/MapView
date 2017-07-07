package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.IMark;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-07-05.
 */

public class BaseMark implements IMark {
    public final static String TAG = "BaseMark";

    //Image
    protected Bitmap bmp;

    //Position
    protected PointF position;
    protected PointF worldPosition;

    //Hit radius
    protected float radius;

    public BaseMark(Bitmap bmp, PointF position) {
        this.bmp = bmp;
        this.position = new PointF(position.x + (bmp.getWidth() / 2), position.y + (bmp.getHeight() / 2));

        //Calculate radius
        radius = bmp.getWidth() > bmp.getHeight() ? bmp.getWidth() / 2 : bmp.getHeight() / 2;

        //Log.d(TAG, "Radius equals: " + radius);
    }

    @Override
    public void update(Matrix m) {
        worldPosition = MapMath.transformPoint(m, position);
    }

    @Override
    public void draw(final Canvas canvas, Paint paint) {
        canvas.drawBitmap(bmp, worldPosition.x - bmp.getWidth() / 2,
                worldPosition.y - bmp.getHeight() / 2, paint);
    }

    @Override
    public boolean hit(PointF position) {
        //Must add half width and half height to position otherwise we are calculating from the topleft corner
        return MapMath.getDistanceBetweenTwoPoints(this.position, position) <= radius;
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
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
