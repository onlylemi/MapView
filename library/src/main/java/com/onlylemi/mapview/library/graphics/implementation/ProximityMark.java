package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.utils.collision.BaseCollision;
import com.onlylemi.mapview.library.utils.collision.MapAxisBox;
import com.onlylemi.mapview.library.utils.collision.MapAxisCircle;

/**
 * Created by patny on 2017-08-02.
 */

public class ProximityMark extends StaticMark {

    /**
     * If this is flagged this mark can only get triggered once and then removed
     */
    private boolean isOneTime = false;

    /**
     * Flagged when this mark is triggered, gets unflagged once we exit
     */
    private boolean triggered = false;

    private BaseCollision collisionMesh;

    public ProximityMark(Bitmap bmp, PointF position, float triggerRadius, boolean isVisible, boolean oneTime) {
        this(bmp, position, triggerRadius, isVisible);
        this.isOneTime = oneTime;
    }

    public ProximityMark(Bitmap bmp, PointF position, float triggerRadius, boolean isVisible) {
        super(bmp, position);
        this.isVisible = isVisible;
        this.collisionMesh = new MapAxisCircle(position, triggerRadius);
    }

    public ProximityMark(Bitmap bmp, PointF position, float colWidth, float colHeight, boolean isVisible, boolean oneTime) {
        this(bmp, position, colWidth, colHeight, isVisible);
        this.isOneTime = oneTime;
    }

    public ProximityMark(Bitmap bmp, PointF position, float colWidth, float colHeight, boolean isVisible) {
        super(bmp, position);
        this.isVisible = isVisible;
        this.collisionMesh = new MapAxisBox(position, colWidth, colHeight);
    }

    @Override
    public void debugDraw(final Matrix m, final Canvas canvas) {
        collisionMesh.debugDraw(m, canvas);
        super.debugDraw(m, canvas);
    }

    /**
     * Called each update and returns true if the position triggers the proximity alert
     * @param position
     * @return
     */
    public boolean triggerProximity(final PointF position) {
        return collisionMesh.isPointInside(position);
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean isTriggered() {
        return  this.triggered;
    }

    public boolean isOneTime() {
        return this.isOneTime;
    }

}
