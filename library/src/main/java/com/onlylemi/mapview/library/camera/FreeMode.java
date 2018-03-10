package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapViewCamera;

/**
 * Created by patnym on 27/12/2017.
 */

public class FreeMode extends BaseMode {
    private static final String TAG = "FreeMode";

    private TouchState currentTouchState = TouchState.TOUCH_STATE_NO;
    private PointF startTouch = new PointF();

    //This represents how long we will stay in this mode until we revert back
    private long timeout;

    float x, y;

    enum TouchState {
        TOUCH_STATE_SCROLL,
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
    public void onInput(int action, PointF point, int extra) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTouch.set(point);
                currentTouchState = TouchState.TOUCH_STATE_SCROLL;
                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                    saveZoom = currentZoom;
//                    startTouch.set(event.getX(0), event.getY(0));
//                    currentTouchState = MapView.TOUCH_STATE_TWO_POINTED;
//
//                    mid = midPoint(event);
//                    oldDist = distance(event, mid);
//                }
//                break;
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
                            //refresh();
                        break;
//                    case MapView.TOUCH_STATE_TWO_POINTED:
//                            oldDist = distance(event, mid);
//                            currentTouchState = MapView.TOUCH_STATE_SCALE;
//                        break;
//                    case MapView.TOUCH_STATE_SCALE:
//                        oldMode = mode == mode.FREE ? oldMode : mode;
//                        currentFreeModeTime = modeOptions.returnFromFreeModeDelayNanoSeconds;
//                        mode = TRACKING_MODE.FREE;
//                        currentMatrix.set(saveMatrix);
//                        newDist = distance(event, mid);
//                        float scale = newDist / oldDist;
//
//                        if (scale * saveZoom < minZoom) {
//                            scale = minZoom / saveZoom;
//                        } else if (scale * saveZoom > maxZoom) {
//                            scale = maxZoom / saveZoom;
//                        }
//                        thread.setZoom(scale * saveZoom);
//
//                        PointF initPoint = isFollowUser ? user.getWorldPosition() : mid;
//
//                        currentMatrix.postScale(scale, scale, initPoint.x, initPoint.y);
//                        thread.setWorldMatrix(currentMatrix);
//                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
