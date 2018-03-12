package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import com.onlylemi.mapview.library.MapViewCamera;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

/**
 * Created by patnym on 27/12/2017.
 */

public class ContainUserMode extends BaseMode {
    private static final String TAG = "ContainUserMode";

    private LocationUser user;

    private float defaultContainZoom;

    private long maxTimeToReturnNano = 500000000l;
    private long defaultTimeToReturn = 50000000l; //Default to spend 0.05 second to translate

    private PointF targetPos;
    private PointF translateDirection;

    private float translateDistance;
    private float zoomDistance;

    private float zoomSpeed;
    private float translateSpeed;

    //Cached values to prevent GC
    private float savedZoom;
    private PointF savedPosition;
    private long timeSpentReturning;

    private PointF lastUserPosition = new PointF();

    public ContainUserMode(MapViewCamera camera, LocationUser user, float defaultContainZoom) {
        super(camera);
        this.user = user;
        this.defaultContainZoom = defaultContainZoom;
    }

    @Override
    public void onStart() {
        initTranslation();
        initZooming();
        timeSpentReturning = maxTimeToReturnNano;
        lastUserPosition.set(user.getPosition());
    }
    
    //// TODO: 2018-03-12 This could perhaps be an internal state to save on the if-statements every update 
    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        if (lastUserPosition.x != user.getPosition().x || lastUserPosition.y != user.getPosition().y) {
            //If we got time left on returning this means the user moved during a init step
            initTranslation(timeSpentReturning > 0 ? timeSpentReturning : defaultTimeToReturn);
        }

        savedPosition = MapUtils.positionFromMatrix(worldMatrix);
        if(translateDistance > 0) {
            translateDistance -= Math.abs(translateSpeed) * deltaTimeNano;
            savedPosition.x += deltaTimeNano * translateSpeed * translateDirection.x;
            savedPosition.y += deltaTimeNano * translateSpeed * translateDirection.y;
            timeSpentReturning -= deltaTimeNano;
        } else {
            savedPosition.x = targetPos.x;
            savedPosition.y = targetPos.y;
        }

        savedZoom = camera.getCurrentZoom();
        if(zoomDistance > 0) {
            zoomDistance -= Math.abs(zoomSpeed) * deltaTimeNano;
            savedZoom += deltaTimeNano * zoomSpeed;
            camera.setCurrentZoom(savedZoom);
        } else {
            savedZoom = defaultContainZoom;
            camera.setCurrentZoom(defaultContainZoom);
        }

        //Because I am just too stupid to use the built in Matrix functions
        float[] ma = { savedZoom, 0, savedPosition.x,
                        0, savedZoom, savedPosition.y,
                        0, 0, 1};
        worldMatrix.setValues(ma);

        lastUserPosition.set(user.getPosition());

        return worldMatrix;
    }

    //This function calculates where we wanna end up
    private PointF calculateTargetPosition() {
        PointF userPositionInMiddleInViewSpace = new PointF((camera.getViewWidth() / 2) - user.getPosition().x,
                (camera.getViewHeight() / 2) - user.getPosition().y);
        Matrix m = new Matrix();
        m.setScale(defaultContainZoom, defaultContainZoom, camera.getViewWidth() / 2, camera.getViewHeight() / 2);
        PointF topLeftCameraCorner = MapMath.transformPoint(m, userPositionInMiddleInViewSpace);

        float targetX = topLeftCameraCorner.x;
        float targetY = topLeftCameraCorner.y;

        if(topLeftCameraCorner.x > 0) {
            targetX = 0;
        } else if(topLeftCameraCorner.x + (camera.getMapWidth() * defaultContainZoom) < camera.getViewWidth()) {
            targetX += camera.getViewWidth() - (topLeftCameraCorner.x + (camera.getMapWidth() * defaultContainZoom));
        }

        if(topLeftCameraCorner.y > 0) {
            targetY = 0;
        } else if(topLeftCameraCorner.y + (camera.getMapHeight() * defaultContainZoom) < camera.getViewHeight()) {
            targetY += camera.getViewHeight() - (topLeftCameraCorner.y + (camera.getMapHeight() * defaultContainZoom));
        }

        return new PointF(targetX, targetY);
    }

    private void initTranslation() {
        initTranslation(maxTimeToReturnNano);
    }

    private void initTranslation(long timeToReturnNano) {
        targetPos = calculateTargetPosition();
        savedPosition = MapUtils.positionFromMatrix(camera.getWorldMatrix());
        translateDirection = MapMath.pointSubtract(targetPos, savedPosition);
        translateDistance = translateDirection.length();
        translateDirection = MapMath.normalize(translateDirection);
        translateSpeed = translateDistance / timeToReturnNano;
    }

    private void initZooming() {
        zoomDistance = defaultContainZoom - camera.getCurrentZoom();
        zoomSpeed = zoomDistance / maxTimeToReturnNano;
        zoomDistance = Math.abs(zoomDistance);
    }

    @Override
    public void onEnd() {

    }
}
