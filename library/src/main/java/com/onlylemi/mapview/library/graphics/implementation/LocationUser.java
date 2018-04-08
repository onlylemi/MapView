package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.BuildConfig;
import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.IBaseAnimation;
import com.onlylemi.mapview.library.graphics.implementation.Animations.RotationAnimation;
import com.onlylemi.mapview.library.graphics.implementation.Animations.TranslationAnimation;
import com.onlylemi.mapview.library.utils.MapMath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patny on 2017-07-05.
 */

public class LocationUser extends BaseGraphics {

    private static final String TAG = "LocationUser";

    //Graphics
    private Bitmap bmp;

    //Rotation - in degrees
    private PointF startDir;

    //Middle position of this graphic in world space
    private PointF worldMidPosition;

    private float radius;

    private List<PointF> moveToDestinations;

    //Animation objects
    //We can only rotate and translate
    private IBaseAnimation rotationAnim = null;
    private List<IBaseAnimation> translationAnims = null;

    //To make this testable
    public LocationUser() {}

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
        this.startDir = new PointF(-1.0f, 0.0f);
        this.setLookAt(lookAt);
        this.worldMidPosition = new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2);
        this.radius = bmp.getHeight() > bmp.getWidth() ? bmp.getHeight() / 2 : bmp.getWidth() / 2;
        this.translationAnims = new ArrayList<>();
        this.moveToDestinations = new ArrayList<>();
    }

    public boolean update(final Matrix m, long deltaTime) {
        worldMidPosition = MapMath.transformPoint(m, position);

        tMatrix.set(mMatrix);

        boolean hasChanged = false;

        //Handle rotation first
        if(rotationAnim != null && !rotationAnim.isDone()) {
            rotationAnim.update(tMatrix, deltaTime);
            hasChanged = true;
        }
        else {
            tMatrix.preRotate(this.rotation, bmp.getWidth() / 2, bmp.getHeight() / 2);
        }
        //Translation last
        if(!translationAnims.isEmpty() && !translationAnims.get(0).isDone()) {
            translationAnims.get(0).update(tMatrix, deltaTime);
            //If animation just finished, remove it
            if(translationAnims.get(0).isDone()) {
                translationAnims.remove(0);
                moveToDestinations.remove(0);
            }
            hasChanged = true;
        } else {
            tMatrix.postTranslate(position.x - bmp.getWidth() / 2, position.y - bmp.getHeight() / 2);
        }



        tMatrix.setValues(MapMath.matrixMultiplication(m, tMatrix));

        return hasChanged;
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

    @Deprecated
    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getWorldPosition() { return  worldMidPosition; }

    /**
     * Moves the graphic to destination over time
     * Will override any current move animation
     * @param destination
     * @param duration time to animate to destination
     */
    public void move(PointF destination, float duration) {
        //Removes all current animations
        translationAnims.clear();
        moveToDestinations.clear();
        translationAnims.add(new TranslationAnimation(this, destination, duration, bmp.getWidth() / 2, bmp.getHeight() / 2));
        moveToDestinations.add(destination);
    }

    /**
     * Moves the graphics to the list of destinations over time
     * @param destinationsLifo list of destinations to move through. Using Last in first out
     * @param duration time to traverse through all destinations
     * @param appendToOldList if an old move isnt finished. Flagged true this will append to an old list if existing
     */
    public void move(List<PointF> destinationsLifo, float duration, boolean appendToOldList) {

        if(destinationsLifo.isEmpty()) {
            throw new IllegalArgumentException("Destination list size = 0. Please include at least 1 element");
        }

        if(destinationsLifo.size() < 1) {
            Log.v(TAG, "Input single element position, calling regular move");
            move(destinationsLifo.get(0), duration);
            return;
        }

        //Replace the list
        translationAnims = new ArrayList<>();

        if(appendToOldList) {
            //Recalculate times for all animations
            moveToDestinations.addAll(destinationsLifo);
        }
        else {
            moveToDestinations = destinationsLifo;
        }

        float totalDistance = 0.0f;
        float[] distances = new float[moveToDestinations.size()];

        //First distance is from the user to point 1
        distances[0] = new PointF(moveToDestinations.get(0).x - position.x, moveToDestinations.get(0).y - position.y).length();
        totalDistance += distances[0];

        //Calculate the distance between each point
        for(int i = 1; i < moveToDestinations.size(); i++) {
            distances[i] = new PointF(moveToDestinations.get(i).x - moveToDestinations.get(i-1).x, moveToDestinations.get(i).y - moveToDestinations.get(i-1).y).length();
            totalDistance += distances[i];
        }
        translationAnims.add(new TranslationAnimation(this, moveToDestinations.get(0), distances[0] / totalDistance * duration, bmp.getWidth() / 2, bmp.getHeight() / 2));
        for(int i = 1; i < moveToDestinations.size(); i++) {
            translationAnims.add(new TranslationAnimation(this, moveToDestinations.get(i-1), moveToDestinations.get(i), distances[i] / totalDistance * duration, bmp.getWidth() / 2, bmp.getHeight() / 2));
        }
    }

    /**
     * Points this graphic directly in the direction of the input vector
     * @param lookAt directional vector (LENGTH == 1)
     */
    public void setLookAt(PointF lookAt) {
        this.rotation = getLookAtAngleFromVector(lookAt);
    }

    /**
     * Animates this graphic to point in the direction of the input vector
     * @param lookAt direction
     * @param duration time to animate to direction
     */
    public void setLookAt(PointF lookAt, float duration) {
        float newRotation = getLookAtAngleFromVector(lookAt);

        if(newRotation != this.rotation) {
            rotationAnim = new RotationAnimation(this, this.rotation, newRotation, new PointF(bmp.getWidth() / 2, bmp.getHeight() / 2), duration);
        }
    }

    //// TODO: 2017-08-08 Move to math?
    private float getLookAtAngleFromVector(PointF lookAt) {
        return (float) Math.toDegrees(Math.atan2(lookAt.x - startDir.x, lookAt.y - startDir.y)) * 2;
    }

}
