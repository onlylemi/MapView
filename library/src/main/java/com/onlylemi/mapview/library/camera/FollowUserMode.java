package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.MapViewCamera;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 27/12/2017.
 */

public class FollowUserMode extends BaseMode {

    private LocationUser user;

    public FollowUserMode(MapViewCamera camera, LocationUser user) {
        super(camera);
        this.user = user;
    }

    @Override
    public void onStart() {

    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
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

        //This is also the distance from our point to the middle
        float distance = desti.length();

        PointF dir = new PointF();

        dir.x = desti.x / distance;
        dir.y = desti.y / distance;

        //Get position from currentMatrix
        float[] m = new float[9];
        worldMatrix.getValues(m);

        //Current position
        PointF pos = new PointF(m[2], m[5]);

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
}
