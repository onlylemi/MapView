package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.*;
import com.onlylemi.mapview.library.utils.MapMath;

import java.util.ArrayList;

/**
 * Created by patny on 2017-08-01.
 */

public class StaticMark extends BaseMark {
    public final static String TAG = "BaseMark";

    //Image
    protected Bitmap bmp;

    protected PointF worldPosition;


    protected ArrayList<PointF> pL = new ArrayList();

    public StaticMark(Bitmap bmp, PointF position) {
        this.bmp = bmp;
        this.position = new PointF(position.x, position.y);

        //Calculate radius
        clickRadius = bmp.getWidth() > bmp.getHeight() ? bmp.getWidth() / 2 : bmp.getHeight() / 2;
    }

    public boolean update(final Matrix m, long deltaTime) {
        worldPosition = MapMath.transformPoint(m, position);

        tMatrix = new Matrix();
        tMatrix.postTranslate(position.x - bmp.getWidth() / 2, position.y - bmp.getHeight() / 2);

        //Dont use a model matrix atm so, fix this later
        tMatrix.setValues(MapMath.matrixMultiplication(m, tMatrix));

        return false;
    }

    public void draw(final Canvas canvas,final Paint paint) {
        canvas.drawBitmap(bmp, tMatrix, paint);
    }

    public void debugDraw(final Matrix m, final Canvas canvas) {
        //Need to scale the radius aswell
        float currentClickRadius = m.mapRadius(clickRadius);

        Paint paint = new Paint();
        paint.setStrokeWidth(0.5f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(worldPosition.x, worldPosition.y, currentClickRadius, paint);
    }

    public boolean hit(final PointF position) {
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
