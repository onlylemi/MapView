package com.onlylemi.mapview.library.camera;

import android.graphics.Matrix;

import com.onlylemi.mapview.library.MapViewCamera;

/**
 * Created by patnym on 27/12/2017.
 */

public class FreeMode extends BaseMode {

    public FreeMode(MapViewCamera camera) {
        super(camera);
    }

    @Override
    public void onStart() {

    }

    @Override
    public Matrix update(Matrix worldMatrix, long deltaTimeNano) {
        return worldMatrix;
    }

    @Override
    public void onEnd() {

    }
}
