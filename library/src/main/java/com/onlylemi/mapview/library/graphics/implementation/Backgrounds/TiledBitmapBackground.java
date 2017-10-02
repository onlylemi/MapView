package com.onlylemi.mapview.library.graphics.implementation.Backgrounds;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import com.onlylemi.mapview.library.graphics.IBackground;

/**
 * Created by patny on 2017-08-29.
 */

public class TiledBitmapBackground implements IBackground {

    private BitmapDrawable background;

    public TiledBitmapBackground(Resources resource, Bitmap bitmap, int viewWidth, int viewHeight) {
        this(resource, bitmap, viewWidth, viewHeight, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }

    public TiledBitmapBackground(Resources resource, Bitmap bitmap, int viewWidth, int viewHeight, Shader.TileMode X, Shader.TileMode Y) {
        background = new BitmapDrawable(resource, bitmap);
        background.setTileModeXY(X, Y);
        background.setBounds(0, 0, viewWidth, viewHeight);
    }

    @Override
    public void draw(Canvas canvas) {
        background.draw(canvas);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        background.setBounds(0, 0, width, height);
    }
}
