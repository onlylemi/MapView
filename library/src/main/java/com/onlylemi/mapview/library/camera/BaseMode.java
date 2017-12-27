package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;

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
     */
    public abstract Matrix update(Matrix worldMatrix, long deltaTimeNano);

    /**
     * Called once we swap mode
     */
    public abstract void onEnd();
}
