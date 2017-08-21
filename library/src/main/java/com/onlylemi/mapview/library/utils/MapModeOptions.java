package com.onlylemi.mapview.library.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by patny on 2017-08-10.
 */

public class MapModeOptions {

    //If defined we use an image
    public Bitmap backgroundImage = null;
    public int backgroundColor = Color.GRAY;

    //RouteLayer options
    public float routeLineWidth = 4.0f;
    public int routeLineColor = Color.BLUE;

    //Everything with Zoom
    //The map calculates the default zoom or the "middle" which will include the entire map within the view.
    //This padding represents any additional zooming above initial value (Entire map within view zoom)
    public float zoomMaxPadding = 2.0f;
    //How much we can zoom out
    public float zoomMinPadding = 0.0f;
    //Pixel padding added to zoom within points
    public float zoomWithinPointsPixelPadding = 150.0f;

    //Everything with delays
    public float returnFromFreeModeDelayNanoSeconds = 2.0f * MapMath.NANOSECOND;

    //Everything with speed
    //Pixels per second
    public float translationsPixelsPerNanoSecond = 400.0f / MapMath.NANOSECOND;

    //How fast we can zoom
    public float zoomPerNanoSecond = 2.0f / MapMath.NANOSECOND;


    public float getReturnFromFreeModeDelaySeconds() {
        return returnFromFreeModeDelayNanoSeconds * MapMath.NANOSECOND;
    }

    public void setReturnFromFreeModeDelaySeconds(float delayInSeconds) {
        returnFromFreeModeDelayNanoSeconds = delayInSeconds * MapMath.NANOSECOND;
    }

    public float getZoomPerSecond() {
        return zoomPerNanoSecond * MapMath.NANOSECOND;
    }

    public void setZoomPerSecond(float zoomPerSecond) {
        this.zoomPerNanoSecond = zoomPerSecond / MapMath.NANOSECOND;
    }

    public float getTranslationsPixelsPerSecond() {
        return translationsPixelsPerNanoSecond * MapMath.NANOSECOND;
    }

    public void setTranslationsPixelsPerSecond(float translationsPixelsPerSecond) {
        this.translationsPixelsPerNanoSecond = translationsPixelsPerSecond / MapMath.NANOSECOND;
    }
}
