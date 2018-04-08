package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.graphics.implementation.LocationUser;

/**
 * Created by patnym on 27/12/2017.
 */

public class FollowUserMode extends BaseContainMode {

    private PointF lastUserPosition;

    private LocationUser user;

    public FollowUserMode(MapViewCamera camera) {
        this(camera, camera.getDefaultContainUserZoom());
    }

    public FollowUserMode(MapViewCamera camera, float zoomLevel) {
        super(camera);
        user = camera.getCurrentUser();
        targetedPosition = new PointF();
        lastUserPosition = new PointF();
        targetedZoom = zoomLevel;
    }

    @Override
    public void onStart() {
        calculateTarget();
        timeSpentReturning = maxTimeToReturnNano;
        super.onStart();
        lastUserPosition.set(user.getPosition());
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        if (lastUserPosition.x != user.getPosition().x || lastUserPosition.y != user.getPosition().y) {
            //If we got time left on returning this means the user moved during a init step
            calculateTarget();
            initTranslation(targetedPosition,
                    timeSpentReturning > 0 ? timeSpentReturning : defaultTimeToReturn);
        } else if(translateDistance <= 0 && zoomDistance <= 0) {
            //If both distances have crossed over we just create our view matrix and return
            return createViewMatrix(worldMatrix);
        }
        return super.update(worldMatrix, deltaTimeNano);
    }

    @Override
    public void onEnd() {

    }

    public void calculateTarget() {
        targetedPosition.x = (camera.getViewWidth() / 2) - (user.getPosition().x * targetedZoom);
        targetedPosition.y = (camera.getViewHeight() / 2) - (user.getPosition().y * targetedZoom);
    }
}
