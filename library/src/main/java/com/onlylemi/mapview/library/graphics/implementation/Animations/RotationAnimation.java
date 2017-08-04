package com.onlylemi.mapview.library.graphics.implementation.Animations;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.IBaseAnimation;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

/**
 * Created by patny on 2017-08-04.
 * Basic rotation animation that interpolates between start angle to end angle
 */

public class RotationAnimation implements IBaseAnimation {

    //Starting angle
    private float currentAngle;

    //End angle
    private float endAngle;

    //Delta angle
    private float distance;

    //Position
    private PointF position;

    //Reference to the object we are animating
    private BaseGraphics baseGraphic;

    private float angularVelocity;
    private boolean done = false;

    /**
     * @param initAngle
     * @param endAngle
     * @param position  point to rotate around
     * @param time      the time it takes to rotate the entire circumference
     */
    public RotationAnimation(float initAngle, float endAngle, PointF position, float time) {
        this.currentAngle = initAngle;
        this.endAngle = endAngle;
        this.position = position;

        this.distance = MapMath.shortestAngleBetweenAngles(initAngle, endAngle);
        float sign = this.distance / Math.abs(this.distance);
        this.distance = Math.abs(this.distance);

        //Calculate the velocity from time
        angularVelocity = 360 / (time * MapMath.NANOSECOND) * sign;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void onInit(BaseGraphics obj) {
        this.baseGraphic = obj;
        done = false;
    }

    @Override
    public Matrix update(Matrix m, long deltaTime) {
        //Interpolate
        float d = angularVelocity * deltaTime;

        //incrementAngle(currentAngle, d);
        currentAngle += d;
        distance -= Math.abs(d);

        if (distance <= 0.0f) {
            baseGraphic.rotation = endAngle;
            currentAngle = endAngle;
            done = true;
        }

        m.preRotate(currentAngle, position.x, position.y);

        baseGraphic.rotation = currentAngle;

        return m;
    }

    @Override
    public void onExit() {
        Log.d("ZUP", "Anim done");
    }
}
