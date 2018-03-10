package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapViewCamera;

/**
 * Created by patnym on 27/12/2017.
 */

public abstract class BaseMode {

    protected MapViewCamera camera;

    public BaseMode(MapViewCamera camera) {
        this.camera = camera;
    }

    /**
     * Called once this mode gets swapped to
     */
    public abstract void onStart();

    /**
     * Named update to be consistent with everything else, gets called every frame
     * @param worldMatrix
     * @param deltaTimeNano
     */
    public abstract Matrix update(Matrix worldMatrix, long deltaTimeNano);

    /**
     * Called once we swap mode
     */
    public abstract void onEnd();

    /**
     * Called on touch input - if not overridden will auto swap to freemode on touch
     * @param action
     * @param point
     * @param extra
     */
    public void onInput(int action, PointF point, int extra) {
        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            camera.switchCameraMode(MapViewCamera.CameraModes.FreeMode);
            camera.resendInput(action, point, extra);
        }
    }
}
