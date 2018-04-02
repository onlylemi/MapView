package com.onlylemi.mapview;

import android.graphics.PointF;

import com.onlylemi.mapview.library.graphics.implementation.LocationUser;

/**
 * Created by patnym on 2018-04-01.
 */

public class MockUser extends LocationUser {

    public MockUser(PointF position) {
        super();
        this.position = position;
    }
}