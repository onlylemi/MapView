package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-07-05.
 */

public class LocationUser extends BaseGraphics {

    //Graphics
    private Bitmap bmp;

    //Rotation - in degrees
    private float rotation = 0.0f;
    private PointF startDir;

    //Middle position of this graphic in world space
    private PointF worldMidPosition;

    //Assumes the bmp looks to the right by default
    public LocationUser(Bitmap bmp, PointF position, PointF lookAt) {
        this(bmp, position, new PointF(1, 0), lookAt);
        mMatrix = new Matrix();
        tMatrix = new Matrix();
    }

    /**
     * Direction must be normalized otherwise unexpected results may occur
     * @param bmp bitmap to render
     * @param position start position
     * @param startDir Direction the bmp starts at (normalized direction (Using point as a vector here))
     * @param lookAt Look at direciton, start rotation of bmp (normalized direction (Using point as a vector here))
     */
    public LocationUser(Bitmap bmp, PointF position, PointF startDir, PointF lookAt) {
        this.bmp = bmp;
        this.position = position;
        this.startDir = startDir;
        this.setLookAt(lookAt);
        this.worldMidPosition = new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2);
    }

    public void update(final Matrix m) {
        worldMidPosition = MapMath.transformPoint(m, position);

        tMatrix.set(mMatrix);
        tMatrix.preRotate(rotation, bmp.getWidth() / 2, bmp.getHeight() / 2);
        tMatrix.postTranslate(position.x - bmp.getWidth() / 2, position.y - bmp.getHeight() / 2);

        tMatrix.setValues(MapMath.matrixMultiplication(m, tMatrix));
    }

    public void draw(final Canvas canvas, final Paint paint) {
        canvas.drawBitmap(bmp, tMatrix, paint);
    }

    public void debugDraw(final Matrix m, final Canvas canvas) {

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

    public PointF getWorldPosition() { return  worldMidPosition; }

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
