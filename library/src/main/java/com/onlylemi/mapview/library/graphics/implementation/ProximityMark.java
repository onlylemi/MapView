package com.onlylemi.mapview.library.graphics.implementation;

import android.graphics.Bitmap;
import android.graphics.PointF;

/**
 * Created by patny on 2017-08-02.
 */

public class ProximityMark extends StaticMark {

    public ProximityMark(Bitmap bmp, PointF position) {
        super(bmp, position);

        isVisible = false;
    }



}
