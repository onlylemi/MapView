package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.MapViewCamera;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 27/12/2017.
 */

public class ContainUserMode extends BaseMode {
    private static final String TAG = "ContainUserMode";

    private LocationUser user;

    private float defualtContainZoom;

    public ContainUserMode(MapViewCamera camera, LocationUser user) {
        super(camera);
        this.user = user;
    }

    @Override
    public void onStart() {
        calculateOnContainUserZoom();
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        float zoom = defualtContainZoom;
        float d = zoom - camera.getCurrentZoom();
        int sign = (int) (d / Math.abs(d));
        d = d * sign; //Absolute distance
        float zVelocity = (2.0f / MapMath.NANOSECOND) * sign * deltaTimeNano;
        d -= Math.abs(zVelocity);

        //move towards target using velocity
        if (d <= 0.0f) {
            camera.zoom(zoom);
        } else {
            camera.zoom(camera.getCurrentZoom() + zVelocity);
        }
        //This is a copy of follow user, this fucking shit needs to get cleaned up soon!
        //My point on the view coordinate system
        PointF dst = new PointF();
        dst.set(user.getPosition());
        float[] b = {dst.x, dst.y};
        worldMatrix.mapPoints(b);

        //My point in view coords
        dst.x = b[0];
        dst.y = b[1];

        //Mid point of the view coordinate system
        PointF trueMid = new PointF(camera.getViewWidth() / 2, camera.getViewHeight() / 2);

        //Direction - NOTE we are going from the mid towards our point because graphics yo
        PointF desti = new PointF(trueMid.x - b[0], trueMid.y - b[1]);

        //Now check if this would put the camera out of bounds
        //// TODO: 2017-09-19 (Nyman): Optimize this shit, do we really need to trasnform the point to find out if its outside the bounds?
        //We actually might since its fucking annoying when we zoom, could this perhaps be done outside world space? Dont bother fix this if it aint laggin!
        Matrix translationMatrix = new Matrix(worldMatrix);
        translationMatrix.postTranslate(desti.x, desti.y);
        PointF cameraPosition = MapMath.transformPoint(translationMatrix, new PointF(0,0));
        PointF cameraBotRight = MapMath.transformPoint(translationMatrix, new PointF(camera.getMapWidth(), camera.getMapHeight()));
        //Check X axis
        if (cameraPosition.x > 0.0f) { //Left side
            PointF currentCameraTopLeft = MapMath.transformPoint(worldMatrix, new PointF(0, 0));
            desti.x = 0 - currentCameraTopLeft.x;
        } else if(cameraBotRight.x < camera.getViewWidth()) { //Right side
            PointF currentCameraBotRight = MapMath.transformPoint(worldMatrix, new PointF(camera.getMapWidth(), 0));
            desti.x =  camera.getViewWidth() - currentCameraBotRight.x;
        }
        //Check Y axis
        if (cameraPosition.y > 0.0f) {
            PointF currentCameraTopLeft = MapMath.transformPoint(worldMatrix, new PointF(0, 0));
            desti.y = 0 - currentCameraTopLeft.y;
        } else if(cameraBotRight.y < camera.getViewHeight()) {
            PointF currentCameraBotRight = MapMath.transformPoint(worldMatrix, new PointF(0, camera.getMapHeight()));
            desti.y = camera.getViewHeight() - currentCameraBotRight.y;
        }

        //This is also the distance from our point to the middle
        float distance = desti.length();


        PointF dir = new PointF();
        dir.x = desti.x / distance;
        dir.y = desti.y / distance;
        distance -= (2500.0f / MapMath.NANOSECOND) * deltaTimeNano;

        if (distance <= 0.0f) {
            worldMatrix.postTranslate(desti.x, desti.y);
        } else {
            worldMatrix.postTranslate(dir.x * (2500.0f / MapMath.NANOSECOND) * deltaTimeNano, dir.y * (2500.0f / MapMath.NANOSECOND) * deltaTimeNano);
        }
        return worldMatrix;
    }

    @Override
    public void onEnd() {

    }

    private void calculateOnContainUserZoom() {
        //Calculate ratios and use the highest
        float widthRatio =  (float) camera.getViewWidth() / camera.getMapWidth();
        float heightRatio = (float) camera.getViewHeight() / camera.getMapHeight();
        if(widthRatio > heightRatio) {
            defualtContainZoom = widthRatio;
        } else {
            defualtContainZoom = heightRatio;
        }
        //Compare with the preset maxZoom and minZoom and make sure we dont go outside, if we do, adjust the user input values
        if(defualtContainZoom < camera.getMinZoom()) {
            camera.setMinZoom(defualtContainZoom);
        } else if(defualtContainZoom > camera.getMaxZoom()) {
            camera.setMaxZoom(defualtContainZoom);
        }
    }
}
