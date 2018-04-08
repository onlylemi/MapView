package com.onlylemi.mapview.library.graphics;

import android.graphics.Canvas;

/**
 * Created by patny on 2017-08-29.
 */

public interface IBackground {
    void draw(Canvas canvas);
    void onSurfaceChanged(int width, int height);
}
