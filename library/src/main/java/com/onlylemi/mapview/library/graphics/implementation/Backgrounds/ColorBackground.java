package com.onlylemi.mapview.library.graphics.implementation.Backgrounds;

import android.graphics.Canvas;

import com.onlylemi.mapview.library.graphics.IBackground;

/**
 * Created by patny on 2017-08-29.
 */

public class ColorBackground implements IBackground {

    private int color;

    public ColorBackground(int color) {
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(color);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
