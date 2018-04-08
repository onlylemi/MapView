package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by patnym on 27/12/2017.
 */

public class ContainPointsMode extends BaseContainMode {

    protected PointF topLeftPoint;
    protected PointF botRightPoint;

    protected float padding;

    public ContainPointsMode(MapViewCamera camera, List<PointF> pointList, float padding) {
        super(camera);
        this.padding = padding;
        float[] minmax = getMaxMinFromPointList(pointList);
        topLeftPoint = new PointF(minmax[2] - padding, minmax[3] - padding);
        botRightPoint = new PointF(minmax[0] + padding, minmax[1] + padding);
    }

    @Override
    public void onStart() {
        init();
        super.onStart();
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        return super.update(worldMatrix, deltaTimeNano);
    }

    @Override
    public void onEnd() {

    }

    protected void init() {
        targetedZoom = getZoomWithinPoints(botRightPoint.x, botRightPoint.y,
                topLeftPoint.x, topLeftPoint.y,
                camera.getViewWidth(), camera.getViewHeight());

        targetedPosition = getTranslationTarget(botRightPoint.x, botRightPoint.y,
                topLeftPoint.x, topLeftPoint.y,
                targetedZoom, camera.getViewWidth(), camera.getViewHeight());
    }

    public static float[] getMaxMinFromPointList(final List<PointF> pointList) {
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

        float[] r = { maxX, maxY, minX, minY};
        return r;
    }

    public static float getZoomWithinPoints(float maxX, float maxY, float minX, float minY,
                                            int viewWidth, int viewHeight) {
        float imageWidth = maxX - minX;
        float imageHeight = maxY - minY;

        float widthRatio = viewWidth / imageWidth;
        float heightRatio = viewHeight / imageHeight;
        float ratio = 0.0f;

        if (widthRatio * imageHeight <= viewHeight) {
            ratio = widthRatio;
        } else if (heightRatio * imageWidth <= viewWidth) {
            ratio = heightRatio;
        }

        return ratio;
    }

    @NonNull
    public static PointF getTranslationTarget(float maxX, float maxY, float minX, float minY,
                                              float zoom, int viewWidth, int viewHeight) {
        float midX = (((maxX - minX) / 2) + minX) * zoom;
        float midY = (((maxY - minY) / 2) + minY) * zoom;
        return new PointF((viewWidth / 2) - midX, (viewHeight / 2) - midY);
    }
}
