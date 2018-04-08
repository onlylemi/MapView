package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.messages.MotionEventMessage;
import com.onlylemi.mapview.library.utils.MapMath;

import java.util.List;

/**
 * Created by patnym on 26/12/2017.
 */
//I'ave kept this class seperate because I wanna add level support in the future
public class MapViewCamera {
    private static final String TAG = "MapViewCamera";

    private Matrix worldMatrix = new Matrix();
    private float currentZoom = 1; //Its just alot easier to keep track of any scaling like this
    private PointF currentPosition = new PointF();

    private CameraModeFactory factory;

    //Holds a reference to the current user we eventually are tracking
    private LocationUser currentUser;

    private BaseMode currentCameraMode;
    private BaseMode previousCameraMode;

    //MapView width and height
    private int viewWidth;
    private int viewHeight;

    //The actual current maplayer width and height
    private int mapWidth;
    private int mapHeight;

    //These are zoom paddings - Multiples, aka 2 = you can zoom in twice the size of the original
    private float maxZoomPaddingFactor = 2.0f;
    private float minZoomPaddingFactor = 0.5f;

    private float maxZoom;
    private float minZoom;

    private long defaultRevertDuration = 5000000000l; //5 seconds
    private float defaultContainUserZoom = 0.0f;

    public enum CameraModes {
        FreeMode,
        ContainUser,
        FollowUser,
        ContainPoints
    }

    public MapViewCamera(int viewWidth, int viewHeight, int mapWidth, int mapHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.factory = new CameraModeFactory(this);
        initZoom(true);
    }

    /**
     * Will initialize/create all modes
     */
    public void initialize(@Nullable LocationUser user) {
        if(user != null) {
            currentUser = user;
        }

        currentCameraMode = factory.createFreeMode();
        currentCameraMode.onStart();
    }

    /**
     * Updates any camera mode and also returns the world matrix
     * @param deltaTimeNano
     * @return
     */
    public Matrix update(long deltaTimeNano) {
        return currentCameraMode.update(worldMatrix, deltaTimeNano);
    }

    /**
     * Reverts to the previous camera mode
     */
    public void revertCameraMode() {
        if(previousCameraMode == null) {
            Log.w(TAG, "Attempted to revert when there was no previous camera mode");
            return;
        }

        Log.d(TAG, "Swapping camera mode from: " + currentCameraMode.toString() + "\n To: " +
                previousCameraMode.toString());

        BaseMode tmp = currentCameraMode;
        currentCameraMode.onEnd();
        currentCameraMode = previousCameraMode;
        previousCameraMode = tmp;
        currentCameraMode.onStart();
    }

    /**
     * Calls on end on the current camera mode and swaps to the input one
     * @param cameraMode
     */
    public void switchCameraMode(BaseMode cameraMode) {
        Log.d(TAG, "Swapping camera mode from: " + currentCameraMode.toString() + "\n To: " +
                cameraMode.toString());

        currentCameraMode.onEnd();
        previousCameraMode = currentCameraMode;
        currentCameraMode = cameraMode;
        currentCameraMode.onStart();
    }

    /**
     * Same as above but more automatic
     * @param mode mode to swap to
     */
    public void switchCameraMode(CameraModes mode, Object... params) {
        BaseMode newMode;
        switch(mode) {
            case FreeMode:
                newMode = factory.createFreeMode();
                break;
            case ContainUser:
                newMode = factory.createContainUserMode();
                break;
            case ContainPoints:
                newMode = factory.createContainPointsMode((List<PointF>) params[0],
                        (boolean) params[1], (float) params[2]);
                break;
            case FollowUser:
                newMode = factory.createFollowUserMode();
                break;
            default:
                Log.e(TAG, "Attempting to swap to an unknown camera mode");
                return;
        }
        switchCameraMode(newMode);
    }

    public void onViewChanged(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
        initZoom(false);

        //Notify the camera modes
        currentCameraMode.onViewChanged();
        if(previousCameraMode != null) {
            previousCameraMode.onViewChanged();
        }
        //Also need to reInitialize the current camera mode
        //Because they might do some init values based on the camera
        currentCameraMode.onStart();
    }

    /**
     * Calculates the min and max zoom values
     * We assume the default starting mode is the entire map in view centered
     * @param initZoomTranslate if true we also set the zoom and translation
     */
    public void initZoom(boolean initZoomTranslate) {
        float widthRatio = (float) viewWidth / mapWidth;
        float heightRatio = (float) viewHeight / mapHeight;

        Log.i(TAG, "widthRatio:" + widthRatio);
        Log.i(TAG, "heightRatio:" + heightRatio);

        float zoom = 1.0f;

        if (widthRatio * mapHeight <= viewHeight) {
            zoom = widthRatio;
        } else if (heightRatio * mapWidth <= viewWidth) {
            zoom = heightRatio;
        }

        defaultContainUserZoom = MapMath.max(widthRatio, heightRatio);
        minZoom = MapMath.min(currentZoom - (currentZoom * minZoomPaddingFactor), defaultContainUserZoom);
        maxZoom = MapMath.max(currentZoom * maxZoomPaddingFactor, defaultContainUserZoom);

        //Notify the factory of our default contain zoom
        factory.setDefaultContainUserZoom(defaultContainUserZoom);

        if(initZoomTranslate) {
            zoom(zoom, 0, 0);
            translate((viewWidth / 2) - ((mapWidth * currentZoom) / 2), (viewHeight / 2) - ((mapHeight * currentZoom) / 2));
        }
    }

    public void translate(float x, float y) {
        currentPosition.x += x;
        currentPosition.y += y;
        worldMatrix.postTranslate(x, y);
    }

    public void zoom(float zoom) {
        zoom(zoom, getViewWidth() / 2, getViewHeight() / 2);
    }

    public void zoom(float zoom, float worldX, float worldY) {
        //float newZoom = MapMath.truncateNumber(zoom, minZoom, maxZoom);
        worldMatrix.postScale(zoom / currentZoom, zoom / currentZoom, worldX, worldY);
        currentZoom = zoom;
    }

    /**
     * This method is used by camera states to resend some input.
     * This helps when swapping to FreeMode for ex. as if we do not resend the input
     * then the user needs to re-touch the screen to move the camera
     * @param action
     * @param event
     */
    public void resendInput(int action, MotionEventMessage event) {
        currentCameraMode.onInput(action, event);
    }

    public void handleInput(int action, MotionEventMessage event) {
        currentCameraMode.onInput(action, event);
    }

    public float[] convertMapXYToScreenXY(float x, float y) {
        Matrix invertMatrix = new Matrix();
        float[] value = {x, y};
        worldMatrix.invert(invertMatrix);
        invertMatrix.mapPoints(value);
        return value;
    }

    //region GETSET

    public float getCurrentZoom() {
        return currentZoom;
    }

    public void setCurrentZoom(float zoom) {
        currentZoom = zoom;
    }

    public PointF getCurrentPosition() {
        return currentPosition;
    }

    public Matrix getWorldMatrix() {
        return worldMatrix;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }

    public float getDefaultContainUserZoom() {
        return defaultContainUserZoom;
    }

    public void setDefaultRevertDuration(long defaultRevertDuration) {
        this.defaultRevertDuration = defaultRevertDuration;
    }

    public LocationUser getCurrentUser() {
        return currentUser;
    }

    public CameraModeFactory getFactory() {
        return factory;
    }

    //endregion GETSET

    //region factory

    public class CameraModeFactory {

        private MapViewCamera camera;

        public CameraModeFactory(MapViewCamera camera) {
            this.camera = camera;
        }

        public ContainUserMode createContainUserMode() {
            return new ContainUserMode(camera, camera.getCurrentUser());
        }

        public FreeMode createFreeMode() {
            return new FreeMode(camera, defaultRevertDuration);
        }

        public FreeMode createFreeMode(long durationNano) {
            return new FreeMode(camera, durationNano);
        }

        public FollowUserMode createFollowUserMode() {
            return new FollowUserMode(camera);
        }

        public FollowUserMode createFollowUserMode(float zoomLevel) {
            return new FollowUserMode(camera, zoomLevel);
        }

        public ContainPointsMode createContainPointsMode(List<PointF> points, boolean containUser, float padding) {
            if(containUser) {
                return new ContainPointsUserMode(camera, points, camera.getCurrentUser(), padding);
            } else {
                return new ContainPointsMode(camera, points, padding);
            }
        }

        public void setDefaultContainUserZoom(float zoom) {
            defaultContainUserZoom = zoom;
        }

    }

    //endregion
}
