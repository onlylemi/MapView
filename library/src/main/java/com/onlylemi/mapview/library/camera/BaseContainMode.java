package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

/**
 * Created by patnym on 2018-03-31.
 */

public class BaseContainMode extends BaseMode {

    protected long maxTimeToReturnNano = 500000000l;
    protected long defaultTimeToReturn = 50000000l; //Default to spend 0.05 second to translate

    protected float savedZoom;
    protected PointF savedPosition;

    protected float translateDistance;
    protected float zoomDistance;

    protected float zoomSpeed;
    protected float translateSpeed;

    protected float targetedZoom;
    protected PointF targetedPosition;

    protected PointF translateDirection;
    protected long timeSpentReturning;

    public BaseContainMode(MapViewCamera camera) {
        super(camera);
    }

    @Override
    public void onStart() {
        initTranslation(targetedPosition, maxTimeToReturnNano);
        initZooming(targetedZoom, maxTimeToReturnNano);
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        translateDistance -= Math.abs(translateSpeed) * deltaTimeNano;
        zoomDistance -= Math.abs(zoomSpeed) * deltaTimeNano;

        savedPosition = MapUtils.positionFromMatrix(worldMatrix);
        if(translateDistance > 0) {
            savedPosition.x += deltaTimeNano * translateSpeed * translateDirection.x;
            savedPosition.y += deltaTimeNano * translateSpeed * translateDirection.y;
            timeSpentReturning -= deltaTimeNano;
        } else {
            savedPosition.x = targetedPosition.x;
            savedPosition.y = targetedPosition.y;
        }

        savedZoom = camera.getCurrentZoom();
        if(zoomDistance > 0) {
            savedZoom += deltaTimeNano * zoomSpeed;
            camera.setCurrentZoom(savedZoom);
        } else {
            savedZoom = targetedZoom;
            camera.setCurrentZoom(targetedZoom);
        }
        return createViewMatrix(worldMatrix);
    }

    protected void initTranslation(PointF targetPos, long timeToReturnNano) {
        savedPosition = MapUtils.positionFromMatrix(camera.getWorldMatrix());
        translateDirection = MapMath.pointSubtract(targetPos, savedPosition);
        translateDistance = translateDirection.length();
        translateDirection = MapMath.normalize(translateDirection);
        translateSpeed = translateDistance / timeToReturnNano;
    }

    protected void initZooming(float targetZoom, long timeToReturnNano) {
        savedZoom = camera.getCurrentZoom();
        zoomDistance = targetZoom - savedZoom;
        zoomSpeed = zoomDistance / timeToReturnNano;
        zoomDistance = Math.abs(zoomDistance);
    }

    protected Matrix createViewMatrix(Matrix m) {
        float[] ma = { savedZoom, 0, savedPosition.x,
                0, savedZoom, savedPosition.y,
                0, 0, 1};
        m.setValues(ma);
        return m;
    }

    @Override
    public void onEnd() {

    }

    //To allow testing
    public float getTargetedZoom() {
        return targetedZoom;
    }

    public PointF getTargetedPosition() {
        return targetedPosition;
    }
}
