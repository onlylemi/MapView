package com.onlylemi.mapview.library.camera;

/**
 * Created by patnym on 2018-03-31.
 */

import android.graphics.Matrix;
import android.graphics.PointF;

import com.onlylemi.mapview.library.graphics.implementation.LocationUser;

import java.util.List;

/**
 * Almost the same as Contain points but also tracks the user
 */
public class ContainPointsUserMode extends ContainPointsMode {

    protected LocationUser user;

    protected PointF originalTopLeft;
    protected PointF originalBotRight;

    protected PointF lastUserPosition;

    public ContainPointsUserMode(MapViewCamera camera, List<PointF> pointList,
                                 LocationUser user, float padding) {
        super(camera, pointList, padding);
        this.user = user;
        lastUserPosition = new PointF();
        originalTopLeft = new PointF(topLeftPoint.x, topLeftPoint.y);
        originalBotRight = new PointF(botRightPoint.x, botRightPoint.y);
        updateExtremesWithPosition(user.getPosition());
    }

    @Override
    public void onStart() {
        timeSpentReturning = maxTimeToReturnNano;
        super.onStart();
        lastUserPosition.set(user.getPosition());
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        //Check if user have moved since last update and if the user is outside the contain points
        if (lastUserPosition.x != user.getPosition().x || lastUserPosition.y != user.getPosition().y) {
            updateExtremesWithPosition(user.getPosition());
            reInitialize();
        } else if(translateDistance <= 0 && zoomDistance <= 0) {
            //If both distances have crossed over we just create our view matrix and return
            return createViewMatrix(worldMatrix);
        }
        lastUserPosition.set(user.getPosition());
        return super.update(worldMatrix, deltaTimeNano);
    }

    private void reInitialize() {
        init();
        initTranslation(targetedPosition,
                timeSpentReturning > 0 ? timeSpentReturning : defaultTimeToReturn);
        initZooming(targetedZoom,
                timeSpentReturning > 0 ? timeSpentReturning : defaultTimeToReturn);
    }

    /**
     * Updates my current extremes comparing them to the given input
     * @param position
     */
    public void updateExtremesWithPosition(PointF position) {
        topLeftPoint.x = Math.min(position.x, originalTopLeft.x) - padding;
        topLeftPoint.y = Math.min(position.y, originalTopLeft.y) - padding;
        botRightPoint.x = Math.max(position.x, originalBotRight.x) + padding;
        botRightPoint.y = Math.max(position.y, originalBotRight.y) + padding;
    }

}
