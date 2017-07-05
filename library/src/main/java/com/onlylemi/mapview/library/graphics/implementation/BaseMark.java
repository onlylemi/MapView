package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.onlylemi.mapview.library.graphics.IMark;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-07-05.
 */

public class BaseMark implements IMark {

    //Image
    final private Bitmap bmp;

    //Position
    final private PointF position;
    private PointF worldPosition;

    public BaseMark(Bitmap bmp, PointF position) {
        this.bmp = bmp;
        this.position = position;
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
}
