package com.onlylemi.mapview.library;

import android.graphics.Bitmap;

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
     * @param bitmap
     */
    void onGetCurrentMap(Bitmap bitmap);
}
