package com.onlylemi.mapview.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.IBackground;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.layer.EmptyMapLayer;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.layer.MapLayer;
import com.onlylemi.mapview.library.messages.ICameraModeCommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;
import com.onlylemi.mapview.library.messages.MotionEventMessage;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapModeOptions;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MapView
 *
 * @author: onlylemi
 */
public class MapView extends SurfaceView implements SurfaceHolder.Callback, Choreographer.FrameCallback {

    private static final String TAG = "MapView";

    //Background color of the canvas
    private int canvasBackgroundColor = -1; //Transparent

    private MapViewListener mapViewListener = null;
    private MapViewSetupCallback setupCallback = null;
    private boolean isMapLoadFinish = false;
    private List<MapBaseLayer> layers; // all layers
    private MapLayer mapLayer;

    private LocationUser user;

    private float minZoom = 0.5f;
    private float maxZoom = 3.0f;

    private PointF startTouch = new PointF();
    private PointF mid = new PointF();
    
    private Matrix saveMatrix = new Matrix();
    private float saveZoom = 0f;

    private static final int TOUCH_STATE_NO = 0; // no touch
    private static final int TOUCH_STATE_SCROLL = 1; // scroll(one point)
    private static final int TOUCH_STATE_SCALE = 2; // scale(two points)
    private static final int TOUCH_STATE_TWO_POINTED = 4; // two points touch
    private int currentTouchState = MapView.TOUCH_STATE_NO; // default touch state

    private float oldDist = 0;
    @Deprecated
    private boolean isFollowUser = false;

    /**
     * Options set by the user, we default the default options
     */
    private MapModeOptions modeOptions = new MapModeOptions();

    //Main rendering thread
    private MapViewRenderer thread = new MapViewRenderer();

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMapView();
    }

    /**
     * init mapview
     */
    private void initMapView() {
        getHolder().addCallback(this);

        layers = new ArrayList<MapBaseLayer>() {
            @Override
            public boolean add(MapBaseLayer layer) {
                if (layers.size() != 0) {
                    if (layer.level >= this.get(this.size() - 1).level) {
                        super.add(layer);
                    } else {
                        for (int i = 0; i < layers.size(); i++) {
                            if (layer.level < this.get(i).level) {
                                super.add(i, layer);
                                break;
                            }
                        }
                    }
                } else {
                    super.add(layer);
                }
                return true;
            }
        };

    }

    /**
     * Suspends teh rendering thread
     */
    public void resumeRendering() {
        Choreographer.getInstance().postFrameCallback(this);
    }

    /**
     * Resumes the rendering thread
     */
    public void pauseRendering() {
        Choreographer.getInstance().removeFrameCallback(this);
    }


    @Override
    public void onVisibilityChanged(View changedView, int state) {
        super.onVisibilityChanged(changedView, state);

        if(thread != null) {
            //Pause rendering if invisible
            if ((state == View.GONE || state == View.INVISIBLE) && thread.getState() != Thread.State.TERMINATED) {
                pauseRendering();
            }else if(state == View.VISIBLE && thread.getState() != Thread.State.TERMINATED) {
                resumeRendering();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (thread == null || thread.getState() == Thread.State.TERMINATED){
            Log.d(TAG, "Creating a new render thread");
            thread = new MapViewRenderer(holder, this);
            thread.setSetupCallback(setupCallback);
            //thread.setRunning(true);
            thread.start();  // Start a new thread
            //onRenderingStarted();
        }
        else if(thread.getState() == Thread.State.NEW){
            Log.d(TAG, "Using an old thread");
            thread.init(holder, this);
            thread.setSetupCallback(setupCallback);
            //thread.setRunning(true);
            thread.start();
            //onRenderingStarted();
        }
        Log.d(TAG, "Surface created, size to: " + getWidth() + "x" + getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface resized to: " + width + "x"+height);
        if(thread != null && thread.getHandler() != null) {
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(),
                    MessageDefenitions.MESSAGE_SURFACE_CHANGED, width, height));
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(thread != null) {
            //Resume thread execution
            Choreographer.getInstance().removeFrameCallback(this);
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(), 0));
            try {
                thread.join();
            }catch (InterruptedException ie) {
                ie.printStackTrace();
            }finally {
                //Let thread finish and exit
                Log.d(TAG, "Rendering thread terminated");
            }
        }
    }

    @Deprecated
    private void onRenderingStarted() {
        thread.waitUntilReady();

        //If any layer has been added before this we create their handlers
        for (MapBaseLayer layer : layers) {
            layer.createHandler(thread);
        }

        calculateOnContainUserZoom();
        if(mapViewListener != null) {
            mapViewListener.onRenderingStarted(getWidth(), getHeight());
        }
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void onSetupCallback(MapViewSetupCallback callback) {
        this.setupCallback = callback;
        this.thread.setSetupCallback(callback);
    }

    /**
     * load map bitmap
     *
     * @param bmp
     */
    @Deprecated
    public void loadMap(final Bitmap bmp) {
        isMapLoadFinish = false;
        mapLayer = new MapLayer(MapView.this);
        layers.add(mapLayer);
        mapLayer.setBmp(bmp);
        if (mapViewListener != null) {
            // load map success, and callback
            mapViewListener.onMapLoadSuccess();
        }
        isMapLoadFinish = true;
    }

    /**
     * This creates an empty canavas to draw your own map on
     * Will not trigger the failed maploading listener as we are not loading anything
     * Triggers the loadSuccess to for consistency
     * @param width
     * @param height
     */
    public void createMap(int width, int height) {
        if(mapLayer == null) {
            mapLayer = new EmptyMapLayer(this, width, height);
            layers.add(mapLayer);
        }
        if(mapViewListener != null) {
            mapViewListener.onMapLoadSuccess();
        }
        isMapLoadFinish = true;
    }

    private TRACKING_MODE oldMode;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(thread != null && thread.getHandler() != null) {
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(),
                    MessageDefenitions.MESSAGE_MOTIONEVENT, MotionEventMessage.MessageFromMotionEvent(event)));
        }
        return true;
    }

    /**
     * Delay until we return to the last known mode when in free mode
     */
    private float currentFreeModeTime = 0.0f;
    private float defualtContainZoom = 2.0f;

    /**
     * update all different modes enabled, like center on user, zoom on points etc
     */
    public void updateModes(float deltaTime) {
        //// TODO: 2017-08-14 this is a hack, when we move these functiosn to seperate modes we will pass the matrix and zoom
        Matrix currentMatrix = thread.getWorldMatrix();
        float currentZoom = thread.getZoom();

        switch (mode) {
            case FREE:
                currentFreeModeTime -= deltaTime;
                if(currentFreeModeTime <= 0.0f) {
                    //Reset mode
                    mode = oldMode;
                }

                break;
            case ZOOM_WITHIN_POINTS: {
                //This is stupid, how do I make this "move" towards a target in a good way?
                //This could in future be state based instead. Just remember the state each time and if it does not update we use the old state
                //// TODO: 2017-08-08 This is a refactor stage later on, this works atm and its fine until a later version
                //Handles the zooming
                float[] minmax = getMaxMinFromPointList(MapUtils.getPositionListFromGraphicList(zoomPoints), modeOptions.zoomWithinPointsPixelPadding);
                float zoom = getZoomWithinPoints(minmax[0], minmax[1], minmax[2], minmax[3]);
                float d = zoom - currentZoom;
                int sign = (int) (d / Math.abs(d));
                d = d * sign; //Absolute distance
                float zVelocity = modeOptions.zoomPerNanoSecond * sign * deltaTime;
                d -= Math.abs(zVelocity);

                //move towards target using velocity
                if (d <= 0.0f) {
                    setCurrentZoom(zoom);
                } else {
                    setCurrentZoom(currentZoom + zVelocity);
                }


                //My point on the view coordinate system
                PointF dst = MapMath.getMidPointBetweenTwoPoints(minmax[0], minmax[1], minmax[2], minmax[3]);
                float[] b = {dst.x, dst.y};
                currentMatrix.mapPoints(b);

                //My point in view coords
                dst.x = b[0];
                dst.y = b[1];

                //Mid point of the view coordinate system
                PointF trueMid = new PointF(getWidth() / 2, getHeight() / 2);

                //Direction - NOTE we are going from the mid towards our point because graphics yo
                PointF desti = new PointF(trueMid.x - b[0], trueMid.y - b[1]);

                //This is also the distance from our point to the middle
                float distance = desti.length();

                PointF dir = new PointF();

                dir.x = desti.x / distance;
                dir.y = desti.y / distance;

                //Get position from currentMatrix
                float[] m = new float[9];
                currentMatrix.getValues(m);

                //Current position
                PointF pos = new PointF(m[2], m[5]);

                distance -= modeOptions.translationsPixelsPerNanoSecond * deltaTime;

                if (distance <= 0.0f) {
                    currentMatrix.postTranslate(desti.x, desti.y);
                } else {
                    currentMatrix.postTranslate(dir.x * modeOptions.translationsPixelsPerNanoSecond * deltaTime, dir.y * modeOptions.translationsPixelsPerNanoSecond * deltaTime);
                }
                thread.setWorldMatrix(currentMatrix);
                break;
            }
            case FOLLOW_USER: {
                //My point on the view coordinate system
                PointF dst = new PointF();
                dst.set(user.getPosition());
                float[] b = {dst.x, dst.y};
                currentMatrix.mapPoints(b);

                //My point in view coords
                dst.x = b[0];
                dst.y = b[1];

                //Mid point of the view coordinate system
                PointF trueMid = new PointF(getWidth() / 2, getHeight() / 2);

                //Direction - NOTE we are going from the mid towards our point because graphics yo
                PointF desti = new PointF(trueMid.x - b[0], trueMid.y - b[1]);

                //This is also the distance from our point to the middle
                float distance = desti.length();

                PointF dir = new PointF();

                dir.x = desti.x / distance;
                dir.y = desti.y / distance;

                //Get position from currentMatrix
                float[] m = new float[9];
                currentMatrix.getValues(m);

                //Current position
                PointF pos = new PointF(m[2], m[5]);

                distance -= modeOptions.translationsPixelsPerNanoSecond * deltaTime;

                if (distance <= 0.0f) {
                    currentMatrix.postTranslate(desti.x, desti.y);
                } else {
                    currentMatrix.postTranslate(dir.x * modeOptions.translationsPixelsPerNanoSecond * deltaTime, dir.y * modeOptions.translationsPixelsPerNanoSecond * deltaTime);
                }

                thread.setWorldMatrix(currentMatrix);
                break;
            }
            case CONTAIN_USER: {
                float zoom = defualtContainZoom;
                float d = zoom - currentZoom;
                int sign = (int) (d / Math.abs(d));
                d = d * sign; //Absolute distance
                float zVelocity = modeOptions.zoomPerNanoSecond * sign * deltaTime;
                d -= Math.abs(zVelocity);

                //move towards target using velocity
                if (d <= 0.0f) {
                    setCurrentZoom(zoom);
                } else {
                    setCurrentZoom(currentZoom + zVelocity);
                }
                //This is a copy of follow user, this fucking shit needs to get cleaned up soon!
                //My point on the view coordinate system
                PointF dst = new PointF();
                dst.set(user.getPosition());
                float[] b = {dst.x, dst.y};
                currentMatrix.mapPoints(b);

                //My point in view coords
                dst.x = b[0];
                dst.y = b[1];

                //Mid point of the view coordinate system
                PointF trueMid = new PointF(getWidth() / 2, getHeight() / 2);

                //Direction - NOTE we are going from the mid towards our point because graphics yo
                PointF desti = new PointF(trueMid.x - b[0], trueMid.y - b[1]);

                //Now check if this would put the camera out of bounds
                //// TODO: 2017-09-19 (Nyman): Optimize this shit, do we really need to trasnform the point to find out if its outside the bounds?
                //We actually might since its fucking annoying when we zoom, could this perhaps be done outside world space? Dont bother fix this if it aint laggin!
                Matrix translationMatrix = new Matrix(currentMatrix);
                translationMatrix.postTranslate(desti.x, desti.y);
                PointF cameraPosition = MapMath.transformPoint(translationMatrix, new PointF(0,0));
                PointF cameraBotRight = MapMath.transformPoint(translationMatrix, new PointF(getMapWidth(), getMapHeight()));
                //Check X axis
                if (cameraPosition.x > 0.0f) { //Left side
                    PointF currentCameraTopLeft = MapMath.transformPoint(currentMatrix, new PointF(0, 0));
                    desti.x = 0 - currentCameraTopLeft.x;
                } else if(cameraBotRight.x < getWidth()) { //Right side
                    PointF currentCameraBotRight = MapMath.transformPoint(currentMatrix, new PointF(getMapWidth(), 0));
                    desti.x =  getWidth() - currentCameraBotRight.x;
                }
                //Check Y axis
                if (cameraPosition.y > 0.0f) {
                    PointF currentCameraTopLeft = MapMath.transformPoint(currentMatrix, new PointF(0, 0));
                    desti.y = 0 - currentCameraTopLeft.y;
                } else if(cameraBotRight.y < getHeight()) {
                    PointF currentCameraBotRight = MapMath.transformPoint(currentMatrix, new PointF(0, getMapHeight()));
                    desti.y = getHeight() - currentCameraBotRight.y;
                }

                //This is also the distance from our point to the middle
                float distance = desti.length();


                PointF dir = new PointF();
                dir.x = desti.x / distance;
                dir.y = desti.y / distance;
                distance -= modeOptions.getTranslationsPixelsPerSecond() * deltaTime;

                if (distance <= 0.0f) {
                    currentMatrix.postTranslate(desti.x, desti.y);
                } else {
                    currentMatrix.postTranslate(dir.x * modeOptions.getTranslationsPixelsPerSecond() * deltaTime, dir.y * modeOptions.getTranslationsPixelsPerSecond() * deltaTime);
                }

                thread.setWorldMatrix(currentMatrix);

                break;
            }
            default:

        }
    }

    /**
     * set mapview listener
     *
     * @param mapViewListener
     */
    public void setMapViewListener(MapViewListener mapViewListener) {
        this.mapViewListener = mapViewListener;
    }

    /**
     * convert coordinate of map to coordinate of screen
     *
     * @param x
     * @param y
     * @return
     */
    public float[] convertMapXYToScreenXY(float x, float y) {
        Matrix invertMatrix = new Matrix();
        float[] value = {x, y};
        thread.getWorldMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(value);
        return value;
    }

    /**
     * map is/not load finish
     *
     * @return
     */
    public boolean isMapLoadFinish() {
        return isMapLoadFinish;
    }

    /**
     * add layer
     *
     * @param layer
     */
    public void addLayer(MapBaseLayer layer) {
        if (layer != null) {

            if(thread != null && thread.getHandler() != null) {
                layer.createHandler(thread);
            }

            layers.add(layer);
        }
    }

    /**
     * get all layers
     *
     * @return
     */
    public List<MapBaseLayer> getLayers() {
        return layers;
    }

    public void translate(float x, float y) {
        Matrix translateMatrix = thread.getWorldMatrix();
        translateMatrix.postTranslate(x, y);
        thread.setWorldMatrix(translateMatrix);
    }

    private PointF position = new PointF(0,0);

    /**
     * set point to map center
     *
     * @param x
     * @param y
     */
    public void mapCenterWithPoint(float x, float y) {
        float[] goal = {x, y};
        Matrix worldMatrix = thread.getWorldMatrix();
        worldMatrix.mapPoints(goal);

        float deltaX = getWidth() / 2 - goal[0];
        float deltaY = getHeight() / 2 - goal[1];
        worldMatrix.postTranslate(deltaX, deltaY);

        thread.setWorldMatrix(worldMatrix);
    }

    @Deprecated
    public float getCurrentZoom() {
        return thread.getZoom();
    }

    @Deprecated
    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    @Deprecated
    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }



    public void calculateOnContainUserZoom() {
        //Calculate ratios and use the highest
        float widthRatio = getWidth() / getMapWidth();
        float heightRatio = getHeight() / getMapHeight();

        if(widthRatio > heightRatio) {
            defualtContainZoom = widthRatio;
        } else {
            defualtContainZoom = heightRatio;
        }
    }

    //This can cause strange behaviours if the input is bad
    @Deprecated
    public void overrideContainUserZoom(float zoom) {
        defualtContainZoom = zoom;
    }

    //Call in onRenderStarted or after
    @Deprecated
    public float getContainUserZoom() {
        return defualtContainZoom;
    }

    public void setCurrentZoom(float zoom) {
        setCurrentZoom(zoom, getWidth() / 2, getHeight() / 2);
    }

    public void setCurrentZoom(float zoom, float x, float y) {
        float scale = MapMath.truncateNumber(zoom, minZoom, maxZoom);

        Matrix scaleMatrix = thread.getWorldMatrix();
        float currentZoom = thread.getZoom();

        scaleMatrix.postScale(scale / currentZoom, scale / currentZoom, x, y);

        thread.setWorldMatrix(scaleMatrix);
        thread.setZoom(scale);
    }

    /**
     * Inits the zooming
     * TODO(Nyman): Should add a padding variable to zooming aswell
     * @param zoom init min zoom
     * @param x
     * @param y
     */
    public void initZoom(float zoom, float x, float y) {
        setCurrentZoom(zoom, x, y);
        minZoom = zoom + modeOptions.zoomMinPadding;
        maxZoom = zoom + modeOptions.zoomMaxPadding;
    }

    /**
     * Takes a set of positions and zoom to the max value that will show all positions.
     * Also moves the camera to the middle position of all positions
     * @param pointList
     */
    public void zoomWithinPoints(final List<PointF> pointList) {
        float[] minmax = getMaxMinFromPointList(pointList, 0.0f);

        setCurrentZoom(getZoomWithinPoints(minmax[0], minmax[1], minmax[2], minmax[3]), 0, 0);
        PointF midPoint = MapMath.getMidPointBetweenTwoPoints(minmax[0], minmax[1], minmax[2], minmax[3]);
        mapCenterWithPoint(midPoint.x, midPoint.y);
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

        float widthRatio = getWidth() / imageWidth;
        float heightRatio = getHeight() / imageHeight;
        float ratio = 0.0f;

        if (widthRatio * imageHeight <= getHeight()) {
            ratio = widthRatio;
        } else if (heightRatio * imageWidth <= getWidth()) {
            ratio = heightRatio;
        }

        return ratio;
    }

    private float time = 0.0f;
    private boolean z = false;
    private List<BaseGraphics> zoomPoints;

    //Default is free
    private TRACKING_MODE mode = TRACKING_MODE.NONE;

    @Override
    public void doFrame(long deltaTimeNano) {
        Choreographer.getInstance().postFrameCallback(this);
        if(thread != null && thread.getHandler() != null)
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(), MessageDefenitions.MESSAGE_DRAW, (int) (deltaTimeNano >> 32), (int) deltaTimeNano));
    }

    public void setContainerUserMode() {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createContainUserMode());
            }
        });
    }

    public void setFreeMode() {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFreeMode());
            }
        });
    }

    public void setFreeMode(final long durationNano) {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFreeMode(durationNano));
            }
        });
    }

    public void setFollowUserMode() {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFollowUserMode());
            }
        });
    }

    public void setContainPointsMode(final List<PointF> points, final boolean includeUser) throws IllegalArgumentException {
        if(includeUser && points.size() < 1)
            throw new IllegalArgumentException("Zoom points size < 1, must include at least 1 point when including a user");
        else if(!includeUser && points.size() <= 1)
            throw new IllegalArgumentException("Zoom points size is less or equals to 1, must be > 1 if no user is included");
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                //// TODO: 27/12/2017 This value shouldnt be hardcoded
                camera.switchCameraMode(camera.getFactory().createContainPointsMode(points, includeUser, 50.0f));
            }
        });
    }

    public void setContainGraphicsPointsMode(final List<? extends BaseGraphics> zoomPoints, final boolean includeUser) throws IllegalArgumentException {
        setContainPointsMode(MapUtils.getPositionListFromGraphicList(zoomPoints), includeUser);
    }

    public void setCameraDefaultRevertDuration(float durationMS) {
        final long durationNano = (long) durationMS * 1000000;
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.setDefaultRevertDuration(durationNano);
            }
        });
    }

    private void sendCameraMessageToThread(ICameraModeCommand command) {
        if(thread != null && thread.getHandler() != null) {
            MessageDefenitions.sendExecuteMessage(thread.getHandler(), MessageDefenitions.MESSAGE_CAMERA_MODE_EXECUTE, command);
        } else {
            Log.w(TAG, "Trying to call a handler method before the thread has finished setup");
        }
    }

    public enum TRACKING_MODE {
        ZOOM_WITHIN_POINTS,
        FOLLOW_USER,
        FREE,
        CONTAIN_USER,
        NONE
    }

    /**
     * Set the zooming points used during zoom mode
     * @param zoomPoints points to include
     * @param zoomSpeed speed in seconds to zoom from full zoom to minimum zoom
     * @param includeUser true if the user (if it exists) should be included in the list of points
     */
    @Deprecated
    public void setZoomPoints(final List<? extends BaseGraphics> zoomPoints, float zoomSpeed, boolean includeUser) throws IllegalArgumentException {
        if(includeUser && zoomPoints.size() < 1)
            throw new IllegalArgumentException("Zoom points size < 1, must include at least 1 point when including a user");
        else if(!includeUser && zoomPoints.size() <= 1)
            throw new IllegalArgumentException("Zoom points size is less or equals to 1, must be > 1 if no user is included");

        this.zoomPoints = new ArrayList<>();

        this.zoomPoints.addAll(zoomPoints);

        if(includeUser) {
            if(user != null)
                this.zoomPoints.add(user);
            else
                throw new IllegalArgumentException("No user object has ben set");
        }
    }

    @Deprecated
    public void setTrackingMode(TRACKING_MODE mode) {
        this.mode = mode;
    }

    private PointF midPoint(MotionEvent event) {
        return MapMath.getMidPointBetweenTwoPoints(event.getX(0), event.getY(0)
                , event.getX(1), event.getY(1));
    }

    private float distance(MotionEvent event, PointF mid) {
        return MapMath.getDistanceBetweenTwoPoints(event.getX(0), event.getY(0)
                , mid.x, mid.y);
    }

    @Deprecated
    private float rotation(MotionEvent event, PointF mid) {
        return MapMath.getDegreeBetweenTwoPoints(event.getX(0), event.getY(0)
                , mid.x, mid.y);
    }

    /**
     * point is/not in floor plan
     *
     * @param x
     * @param y
     * @return
     */
    @Deprecated
    public boolean withFloorPlan(float x, float y) {
        float[] goal = convertMapXYToScreenXY(x, y);
        return goal[0] > 0 && goal[0] < mapLayer.getDimensions().width() && goal[1] > 0
                && goal[1] < mapLayer.getDimensions().height();
    }

    @Deprecated
    public float getMapWidth() {
        return mapLayer.getDimensions().width();
    }

    @Deprecated
    public float getMapHeight() {
        return mapLayer.getDimensions().height();
    }

    @Deprecated
    public int getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    @Deprecated
    public void setCanvasBackgroundColor(int canvasBackgroundColor) {
        this.canvasBackgroundColor = canvasBackgroundColor;
    }

    public void setUser(LocationUser user) {
        this.user = user;
    }

    @Deprecated
    public void centerOnUser(LocationUser user) {
        mapCenterWithPoint(user.getPosition().x, user.getPosition().y);
        this.user = user;
        this.isFollowUser = true;
    }

    @Deprecated
    public void disableCenterOnUser() {
        this.isFollowUser = false;
    }

    @Deprecated
    public void setMapModeOptions(MapModeOptions mapModeOptions) {
        modeOptions = mapModeOptions;
    }

    @Deprecated
    public MapModeOptions getMapModeOptions() {
        return modeOptions;
    }

    @Deprecated
    public void setBackground(IBackground background) {
        thread.setBackground(background);
    }

    @Deprecated
    public void setFixedFPS(int FPS) throws InstantiationError {
//        if(thread != null) {
//            thread.setFixedFrameRate(FPS);
//        }
    }

    @Deprecated
    public void disableFixedFPS() {
//        if(thread != null) {
//            thread.disableFixedFrameRate();
//        }
    }

    //region debugging

    public void setDebug(boolean enableDebug) {
        thread.setDebug(enableDebug);
    }

    //endregion

}
