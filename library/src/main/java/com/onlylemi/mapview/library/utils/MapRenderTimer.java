package com.onlylemi.mapview.library.utils;

/**
 * Created by patny on 2017-08-14.
 */

public class MapRenderTimer {

    private long currentTimeNano;
    private long deltaNano;


    public void start() {
        currentTimeNano = System.nanoTime();
    }

    public void update() {
        deltaNano = System.nanoTime() - currentTimeNano;
        currentTimeNano = System.nanoTime();
    }

    public long getFrameTimeNano() {
        return deltaNano;
    }

    //Do we need stop?
}
