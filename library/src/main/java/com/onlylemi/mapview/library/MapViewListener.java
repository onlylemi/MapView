package com.onlylemi.mapview.library;

/**
 * MapViewListener
 *
 * @author: onlylemi
 */
public interface MapViewListener {

    /**
     * when mapview load complete to callback
     */
    void onMapLoadSuccess();

    /**
     * when mapview load error to callback
     */
    void onMapLoadFail();

    /**
     * when the thread is started and running
     */
    void onRenderingStarted(int viewWidth, int viewHeight);
}
