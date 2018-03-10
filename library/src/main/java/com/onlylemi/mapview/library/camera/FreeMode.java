package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapViewCamera;
import com.onlylemi.mapview.library.utils.MapMath;

/**
 * Created by patnym on 27/12/2017.
 */

public class FreeMode extends BaseMode {
    private static final String TAG = "FreeMode";

    private TouchState currentTouchState = TouchState.TOUCH_STATE_NO;
    private PointF startTouch = new PointF();
    private PointF midPoint = new PointF();
    private float saveZoom;
    private float oldDistance;
    private float newDistance;

    //This represents how long we will stay in this mode until we revert back
    private long timeout;

    float x, y;

    enum TouchState {
        TOUCH_STATE_SCROLL,
        TOUCH_STATE_TWO_POINTED,
        TOUCH_STATE_SCALE,
        TOUCH_STATE_NO
    }

//    public FreeMode(MapViewCamera camera) {
//        super(camera);
//        timeout = Long.MAX_VALUE; //This is a bit of a haxx but should do the job
//    }

    public FreeMode(MapViewCamera camera, long durationNano) {
        super(camera);
        timeout = durationNano;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "Entered free mode");
    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        worldMatrix.postTranslate(x, y);
        x = 0;
        y = 0;

        timeout -= deltaTimeNano;
        if(timeout < 0) {
            camera.revertCameraMode();
        }

        return worldMatrix;
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onInput(int action, PointF point, Object... extra) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTouch.set(point);
                currentTouchState = TouchState.TOUCH_STATE_SCROLL;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                    saveZoom = camera.getCurrentZoom();
                    currentTouchState = TouchState.TOUCH_STATE_TWO_POINTED;

                    midPoint = MapMath.getMidPointBetweenTwoPoints(point.x, point.y
                            , ((MotionEvent)extra[0]).getX(1), ((MotionEvent)extra[0]).getY(1));
                    startTouch.set(midPoint);
                break;
            case MotionEvent.ACTION_UP:
                currentTouchState = TouchState.TOUCH_STATE_NO;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                currentTouchState = TouchState.TOUCH_STATE_NO;
                break;
            case MotionEvent.ACTION_MOVE:
                switch (currentTouchState) {
                    case TOUCH_STATE_SCROLL:
                            x = point.x - startTouch.x;
                            y = point.y - startTouch.y;
                            startTouch.set(point);
                        break;
                    case TOUCH_STATE_TWO_POINTED:
                            oldDistance = MapMath.getDistanceBetweenTwoPoints(((MotionEvent)extra[0]).getX(0),
                                    ((MotionEvent)extra[0]).getY(0) , midPoint.x, midPoint.y);
                            currentTouchState = TouchState.TOUCH_STATE_SCALE;
                        break;
                    case TOUCH_STATE_SCALE:
                        newDistance = MapMath.getDistanceBetweenTwoPoints(((MotionEvent)extra[0]).getX(0),
                                ((MotionEvent)extra[0]).getY(0) , midPoint.x, midPoint.y);
                        float scale = newDistance / oldDistance;
                        camera.zoom(scale * saveZoom, startTouch.x, startTouch.y);
//                        if (scale * saveZoom < minZoom) {
//                            scale = minZoom / saveZoom;
//                        } else if (scale * saveZoom > maxZoom) {
//                            scale = maxZoom / saveZoom;
//                        }
//                        currentMatrix.postScale(scale, scale, initPoint.x, initPoint.y);
//                        thread.setWorldMatrix(currentMatrix);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
