package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.MapViewCamera;
import com.onlylemi.mapview.library.utils.MapMath;

import java.util.List;

/**
 * Created by patnym on 27/12/2017.
 */

public class ContainPointsMode extends BaseMode {

    private float targetZoom;
    private PointF targetDst;

    public ContainPointsMode(MapViewCamera camera, List<PointF> pointList, float padding) {
        super(camera);
        float[] minmax = getMaxMinFromPointList(pointList, padding);
        targetZoom = getZoomWithinPoints(minmax[0], minmax[1], minmax[2], minmax[3]);
        targetDst = MapMath.getMidPointBetweenTwoPoints(minmax[0], minmax[1], minmax[2], minmax[3]);
    }

    @Override
    public void onStart() {
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        //This is stupid, how do I make this "move" towards a target in a good way?
        //This could in future be state based instead. Just remember the state each time and if it does not update we use the old state
        //// TODO: 2017-08-08 This is a refactor stage later on, this works atm and its fine until a later version
        //Handles the zooming
        float d = targetZoom - camera.getCurrentZoom();
        int sign = (int) (d / Math.abs(d));
        d = d * sign; //Absolute distance
        float zVelocity = (2.0f / MapMath.NANOSECOND) * sign * deltaTimeNano;
        d -= Math.abs(zVelocity);

        //move towards target using velocity
        if (d <= 0.0f) {
            camera.zoom(targetZoom);
        } else {
            camera.zoom(camera.getCurrentZoom() + zVelocity);
        }


        //My point on the view coordinate system
        float[] b = {targetDst.x, targetDst.y};
        worldMatrix.mapPoints(b);

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

    private float[] getMaxMinFromPointList(final List<PointF> pointList, float padding) {
        PointF initPoint = pointList.get(0);

        //Find max point height and max point width
        float maxX = initPoint.x;
        float minX = initPoint.x;

        float maxY = initPoint.y;
        float minY = initPoint.y;

        for(PointF p : pointList) {
            //MAX
            maxX = p.x > maxX ? p.x : maxX;
            maxY = p.y > maxY ? p.y : maxY;

            //MIN
            minX = p.x < minX ? p.x : minX;
            minY = p.y < minY ? p.y : minY;
        }

        float[] r = { maxX + padding, maxY + padding, minX - padding, minY - padding};
        return r;
    }

    private float getZoomWithinPoints(float maxX, float maxY, float minX, float minY) {
        float imageWidth = maxX - minX;
        float imageHeight = maxY - minY;

        float widthRatio = camera.getViewWidth() / imageWidth;
        float heightRatio = camera.getViewHeight() / imageHeight;
        float ratio = 0.0f;

        if (widthRatio * imageHeight <= camera.getViewHeight()) {
            ratio = widthRatio;
        } else if (heightRatio * imageWidth <= camera.getViewWidth()) {
            ratio = heightRatio;
        }

        return ratio;
    }
}
