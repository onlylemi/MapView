package com.onlylemi.mapview.library.graphics.implementation.Animations;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.IBaseAnimation;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patny on 2017-08-08.
 */

public class TranslationAnimation implements IBaseAnimation {


    private boolean done = false;

    private float velocityX, velocityY, vecloity;

    private float distance = 0.0f;

    private BaseGraphics baseGraphics;

    private PointF currentPosition, destination;

    private float offsetX, offsetY;

    public TranslationAnimation(BaseGraphics baseGraphics, PointF startPosition, PointF destination, float duration, float offsetX, float offsetY) {
        this.baseGraphics = baseGraphics;
        this.currentPosition = startPosition;
        this.destination = destination;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        //This special case is needed if we try to move to the same location we're on.
        if(duration != 0) {
            //Calculate distance
            this.distance = MapMath.getDistanceBetweenTwoPoints(this.currentPosition, destination);

            //Calculate speed
            this.vecloity = this.distance / (duration * MapMath.NANOSECOND);
            this.velocityX = (destination.x - this.currentPosition.x) / (duration * MapMath.NANOSECOND);
            this.velocityY = (destination.y - this.currentPosition.y) / (duration * MapMath.NANOSECOND);
        } else {
            //Everything defaults to 0
            this.distance = 0.0f;
            this.vecloity = 0.0f;
            this.velocityX = 0.0f;
            this.velocityY = 0.0f;
        }
    }

    public TranslationAnimation(BaseGraphics baseGraphics, PointF destination, float duration, float offsetX, float offsetY) {
        this(baseGraphics, baseGraphics.position, destination, duration, offsetX, offsetY);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public Matrix update(Matrix m, long deltaTime) {

        float dX = this.velocityX * deltaTime;
        float dY = this.velocityY * deltaTime;
        float d = this.vecloity * deltaTime;

        this.distance -= d;
        this.currentPosition.x += dX;
        this.currentPosition.y += dY;

        if(this.distance <= 0.0f) {
            this.currentPosition = this.destination;
            done = true;
        }

        baseGraphics.position = this.currentPosition;
        m.postTranslate(this.currentPosition.x - offsetX, this.currentPosition.y - offsetY);



        return m;
    }
}
