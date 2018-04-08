package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 27/12/2017.
 */

public class ContainUserMode extends BaseContainMode {
    private static final String TAG = "ContainUserMode";

    private LocationUser user;

    private PointF lastUserPosition;

    public ContainUserMode(MapViewCamera camera, LocationUser user) {
        super(camera);
        this.user = user;
        lastUserPosition = new PointF();
    }

    @Override
    public void onViewChanged() {
    }

    @Override
    public void onStart() {
        targetedZoom = camera.getDefaultContainUserZoom();
        targetedPosition = calculateTargetPosition(targetedZoom);
        timeSpentReturning = maxTimeToReturnNano;
        lastUserPosition.set(user.getPosition());
        super.onStart();
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        if (lastUserPosition.x != user.getPosition().x || lastUserPosition.y != user.getPosition().y) {
            //If we got time left on returning this means the user moved during a init step
            targetedPosition = calculateTargetPosition(targetedZoom);
            initTranslation(targetedPosition,
                    timeSpentReturning > 0 ? timeSpentReturning : defaultTimeToReturn);
        } else if(translateDistance <= 0 && zoomDistance <= 0) {
            //If both distances have crossed over we just create our view matrix and return
            return createViewMatrix(worldMatrix);
        }
        lastUserPosition.set(user.getPosition());
        return super.update(worldMatrix, deltaTimeNano);
    }

    //This function calculates where we wanna end up
    @NonNull
    private PointF calculateTargetPosition(float targetZoom) {
        PointF userPositionInMiddleInViewSpace = new PointF((camera.getViewWidth() / 2) - user.getPosition().x,
                (camera.getViewHeight() / 2) - user.getPosition().y);
        Matrix m = new Matrix();
        m.setScale(targetZoom, targetZoom, camera.getViewWidth() / 2, camera.getViewHeight() / 2);
        PointF topLeftCameraCorner = MapMath.transformPoint(m, userPositionInMiddleInViewSpace);

        float targetX = topLeftCameraCorner.x;
        float targetY = topLeftCameraCorner.y;

        if(topLeftCameraCorner.x > 0) {
            targetX = 0;
        } else if(topLeftCameraCorner.x + (camera.getMapWidth() * targetZoom) < camera.getViewWidth()) {
            targetX += camera.getViewWidth() - (topLeftCameraCorner.x + (camera.getMapWidth() * targetZoom));
        }

        if(topLeftCameraCorner.y > 0) {
            targetY = 0;
        } else if(topLeftCameraCorner.y + (camera.getMapHeight() * targetZoom) < camera.getViewHeight()) {
            targetY += camera.getViewHeight() - (topLeftCameraCorner.y + (camera.getMapHeight() * targetZoom));
        }

        return new PointF(targetX, targetY);
    }

    @Override
    public void onEnd() {

    }
}
