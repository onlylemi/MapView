package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.ILocationUser;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-07-05.
 */

public class BaseUser implements ILocationUser {

    //Graphics
    private Bitmap bmp;

    //Position
    private PointF position;
    private PointF worldPosition;

    //Rotation - in degrees
    private float rotation = 0.0f;
    private PointF startDir;

    //Assumes the bmp looks to the right by default
    public BaseUser(Bitmap bmp, PointF position, PointF lookAt) {
        this(bmp, position, new PointF(1, 0), lookAt);
    }

    /**
     * Direction must be normalized otherwise unexpected results may occur
     * @param bmp bitmap to render
     * @param position start position
     * @param startDir Direction the bmp starts at (normalized direction (Using point as a vector here))
     * @param lookAt Look at direciton, start rotation of bmp (normalized direction (Using point as a vector here))
     */
    public BaseUser(Bitmap bmp, PointF position,PointF startDir, PointF lookAt) {
        this.bmp = bmp;
        this.position = position;
        this.startDir = startDir;
        this.setLookAt(lookAt);
    }

    @Override
    public void update(Matrix m) {
        worldPosition = MapMath.transformPoint(m, position);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Matrix m = new Matrix();
        m.preRotate(rotation, bmp.getWidth() / 2, bmp.getHeight() / 2);
        m.postTranslate(worldPosition.x - bmp.getWidth() / 2, worldPosition.y - bmp.getHeight() / 2);
        canvas.drawBitmap(bmp, m, paint);
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

    public void setLookAt(PointF lookAt) {
        //Determine direction of rotation
        float dir = (lookAt.x * startDir.y) - (lookAt.y * startDir.x);
        //Get the sign
        int sign = (int) (dir / (Math.abs(dir)));
        //Correction as sign can be 0 at 180 degrees turn
        sign = sign == 0 ? 1 : sign;
        this.rotation = (float) Math.toDegrees(Math.acos((lookAt.x * startDir.x) + (lookAt.y * startDir.y))) * sign ;
    }

}
