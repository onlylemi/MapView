package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.ILocationUser;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

/**
 * Created by patny on 2017-07-05.
 */

public class BaseUser implements ILocationUser {

    //Graphics
    private Bitmap bmp;

    //Position
    private PointF position;
    private PointF worldMidPoint;

    //Rotation - in degrees
    private float rotation = 0.0f;
    private PointF startDir;

    private Matrix mMatrix;

    private Matrix tMatrix;

    //Assumes the bmp looks to the right by default
    public BaseUser(Bitmap bmp, PointF position, PointF lookAt) {
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
    public BaseUser(Bitmap bmp, PointF position,PointF startDir, PointF lookAt) {
        this.bmp = bmp;
        this.position = position;
        this.startDir = startDir;
        this.setLookAt(lookAt);
    }

    @Override
    public void update(Matrix m) {
        worldMidPoint = MapMath.transformPoint(m, position);

        //Log.d("User", "World position: " + worldPosition.toString());

        tMatrix.set(mMatrix);
        tMatrix.preRotate(rotation, bmp.getWidth() / 2, bmp.getHeight() / 2);
        tMatrix.postTranslate(position.x - bmp.getWidth() / 2, position.y - bmp.getHeight() / 2);

        float[] A = new float[9];
        float[] B = new float[9];

        tMatrix.getValues(A);
        m.getValues(B);

        tMatrix.setValues(matrixMultiplication(B, A));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bmp, tMatrix, paint);
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

    /**
     * Returns the middle point of the bitmap in world space
     * @return
     */
    public PointF getWorldPosition() { return  worldMidPoint; }

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

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
        return result;
    }

}
