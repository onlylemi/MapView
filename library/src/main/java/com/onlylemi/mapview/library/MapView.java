package com.onlylemi.mapview.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.onlylemi.mapview.library.graphics.ILocationUser;
import com.onlylemi.mapview.library.graphics.implementation.BaseUser;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.layer.MapLayer;
import com.onlylemi.mapview.library.utils.MapAABB;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * MapView
 *
 * @author: onlylemi
 */
public class MapView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "MapView";

    private SurfaceHolder holder;
    private MapViewListener mapViewListener = null;
    private boolean isMapLoadFinish = false;
    private List<MapBaseLayer> layers; // all layers
    private MapLayer mapLayer;

    private BaseUser user;

    private Canvas canvas;

    private float minZoom = 0.5f;
    private float maxZoom = 3.0f;

    private PointF startTouch = new PointF();
    private PointF mid = new PointF();
    
    private Matrix saveMatrix = new Matrix();
    private Matrix currentMatrix = new Matrix();
    private float currentZoom = 1.0f;
    private float saveZoom = 0f;
    private float currentRotateDegrees = 0.0f;
    private float saveRotateDegrees = 0.0f;
    private MapAABB restrictiveBoundingBox;

    private static final int TOUCH_STATE_NO = 0; // no touch
    private static final int TOUCH_STATE_SCROLL = 1; // scroll(one point)
    private static final int TOUCH_STATE_SCALE = 2; // scale(two points)
    private static final int TOUCH_STATE_ROTATE = 3; // rotate(two points)
    private static final int TOUCH_STATE_TWO_POINTED = 4; // two points touch
    private int currentTouchState = MapView.TOUCH_STATE_NO; // default touch state

    private float oldDist = 0, oldDegree = 0;
    private boolean isScaleAndRotateTogether = false;
    private boolean isOverflowing = false; //Indicates if the map model is bigger then the viewport
    private boolean isRestrictedView = false;
    private boolean isFollowUser = false;

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
        Log.i(TAG, "Init Width " + getWidth());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        restrictiveBoundingBox = new MapAABB(new PointF(0, 0), getWidth(), getHeight());
        Log.d(TAG, "MapView AABB inited");
        refresh();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * reload mapview
     */
    public void refresh() {
        //Testing
        if(restrictiveBoundingBox != null && restrictiveBoundingBox.isFullyIntersecting(mapLayer.getMapBoundingBox())) {
            isOverflowing = false;
        } else {
            isOverflowing = true;
        }

        if (holder != null) {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(-1);
                if (isMapLoadFinish) {
                    for (MapBaseLayer layer : layers) {
                        if (layer.isVisible) {
                            layer.draw(canvas, currentMatrix, currentZoom, currentRotateDegrees);
                        }
                    }
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void loadMap(Bitmap bitmap) {
        loadMap(MapUtils.getPictureFromBitmap(bitmap));
    }

    /**
     * load map image
     *
     * @param picture
     */
    public void loadMap(final Picture picture) {
        isMapLoadFinish = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (picture != null) {
                    if (mapLayer == null) {
                        mapLayer = new MapLayer(MapView.this);
                        // add map image layer
                        layers.add(mapLayer);
                    }
                    mapLayer.setImage(picture);
                    if (mapViewListener != null) {
                        // load map success, and callback
                        mapViewListener.onMapLoadSuccess();
                    }
                    isMapLoadFinish = true;
                    refresh();
                } else {
                    if (mapViewListener != null) {
                        mapViewListener.onMapLoadFail();
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isMapLoadFinish) {
            return false;
        }

        float newDist;
        float newDegree;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(currentMatrix);
                startTouch.set(event.getX(), event.getY());
                currentTouchState = MapView.TOUCH_STATE_SCROLL;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    saveMatrix.set(currentMatrix);
                    saveZoom = currentZoom;
                    startTouch.set(event.getX(0), event.getY(0));
                    currentTouchState = MapView.TOUCH_STATE_TWO_POINTED;

                    mid = midPoint(event);
                    oldDist = distance(event, mid);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (withFloorPlan(event.getX(), event.getY())) {
                    for (MapBaseLayer layer : layers) {
                        layer.onTouch(event);
                    }
                }
                currentTouchState = MapView.TOUCH_STATE_NO;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                currentTouchState = MapView.TOUCH_STATE_NO;
                break;
            case MotionEvent.ACTION_MOVE:
                switch (currentTouchState) {
                    case MapView.TOUCH_STATE_SCROLL:

                        if(isOverflowing) {
                            currentMatrix.set(saveMatrix);
                            translate(event.getX() - startTouch.x, event.getY() -
                                    startTouch.y);
                            refresh();
                        }
                        break;
                    case MapView.TOUCH_STATE_TWO_POINTED:
                            oldDist = distance(event, mid);
                            currentTouchState = MapView.TOUCH_STATE_SCALE;
                        break;
                    case MapView.TOUCH_STATE_SCALE:
                        currentMatrix.set(saveMatrix);
                        newDist = distance(event, mid);
                        float scale = newDist / oldDist;

                        if (scale * saveZoom < minZoom) {
                            scale = minZoom / saveZoom;
                        } else if (scale * saveZoom > maxZoom) {
                            scale = maxZoom / saveZoom;
                        }
                        currentZoom = scale * saveZoom;
                        currentMatrix.postScale(scale, scale, user.getWorldPosition().x, user.getWorldPosition().y);
                        refresh();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setRestrictMapToView(boolean restrict) {
        this.isRestrictedView = restrict;
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
        currentMatrix.invert(invertMatrix);
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
        currentMatrix.postTranslate(x, y);
    }

    /**
     * set point to map center
     *
     * @param x
     * @param y
     */
    public void mapCenterWithPoint(float x, float y) {
        float[] goal = {x, y};
        currentMatrix.mapPoints(goal);

        float deltaX = getWidth() / 2 - goal[0];
        float deltaY = getHeight() / 2 - goal[1];
        currentMatrix.postTranslate(deltaX, deltaY);
    }

    public float getCurrentRotateDegrees() {
        return currentRotateDegrees;
    }

    /**
     * set rotate degrees
     *
     * @param degrees
     */
    public void setCurrentRotateDegrees(float degrees) {
        mapCenterWithPoint(getMapWidth() / 2, getMapHeight() / 2);
        setCurrentRotateDegrees(degrees, getWidth() / 2, getHeight() / 2);
    }

    /**
     * set rotate degrees
     *
     * @param degrees
     * @param x
     * @param y
     */
    public void setCurrentRotateDegrees(float degrees, float x, float y) {
        currentMatrix.postRotate(degrees - currentRotateDegrees, x, y);

        currentRotateDegrees = degrees % 360;
        currentRotateDegrees = currentRotateDegrees > 0 ? currentRotateDegrees :
                currentRotateDegrees + 360;
    }

    public float getCurrentZoom() {
        return currentZoom;
    }

    public boolean isScaleAndRotateTogether() {
        return isScaleAndRotateTogether;
    }

    /**
     * setting scale&rotate is/not together on touch
     *
     * @param scaleAndRotateTogether
     */
    public void setScaleAndRotateTogether(boolean scaleAndRotateTogether) {
        isScaleAndRotateTogether = scaleAndRotateTogether;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }

    public void setCurrentZoom(float zoom) {
        setCurrentZoom(zoom, getWidth() / 2, getHeight() / 2);
    }

    public void setCurrentZoom(float zoom, float x, float y) {
        currentMatrix.postScale(zoom / this.currentZoom, zoom / this.currentZoom, x, y);
        this.currentZoom = zoom;
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

        final float maxZoomPadding = 2.0f;
        final float minZoomPadding = 0.0f;

        minZoom = zoom + minZoomPadding;
        maxZoom = zoom + maxZoomPadding;
    }

    private PointF midPoint(MotionEvent event) {
        return MapMath.getMidPointBetweenTwoPoints(event.getX(0), event.getY(0)
                , event.getX(1), event.getY(1));
    }

    private float distance(MotionEvent event, PointF mid) {
        return MapMath.getDistanceBetweenTwoPoints(event.getX(0), event.getY(0)
                , mid.x, mid.y);
    }

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
    public boolean withFloorPlan(float x, float y) {
        float[] goal = convertMapXYToScreenXY(x, y);
        return goal[0] > 0 && goal[0] < mapLayer.getImage().getWidth() && goal[1] > 0
                && goal[1] < mapLayer.getImage().getHeight();
    }

    public float getMapWidth() {
        return mapLayer.getImage().getWidth();
    }

    public float getMapHeight() {
        return mapLayer.getImage().getHeight();
    }

    public Matrix getCurrentTransform() { return currentMatrix; }

    public void centerOnUser(BaseUser user) {
        //Get the middle position of screen
        mapCenterWithPoint(user.getPosition().x, user.getPosition().y);
        this.user = user;
    }

}
