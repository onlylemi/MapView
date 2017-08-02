package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-08-02.
 */

public class ProximityMark extends StaticMark {

    /**
     * When the proximity alert shall trigger, recommended to use the bmp to calculate this size
     */
    private float triggerRadius;

    public ProximityMark(Bitmap bmp, PointF position, float triggerRadius, boolean isVisible) {
        super(bmp, position);

        this.triggerRadius = triggerRadius;

        this.isVisible = isVisible;
    }

    @Override
    public void debugDraw(final Matrix m, final Canvas canvas) {
        //Need to scale the radius aswell
        float currentClickRadius = m.mapRadius(triggerRadius);

        Paint paint = new Paint();
        paint.setStrokeWidth(0.5f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(worldPosition.x, worldPosition.y, currentClickRadius, paint);
    }

    /**
     * Called each update and returns true if the position triggers the proximity alert
     * @param position
     * @return
     */
    public boolean triggerProximity(final PointF position) {
        return MapMath.getDistanceBetweenTwoPoints(this.position, position) < triggerRadius;
    }

}
