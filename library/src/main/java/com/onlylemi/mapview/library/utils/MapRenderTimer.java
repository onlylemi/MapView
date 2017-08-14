package com.onlylemi.mapview.library.utils;

/**
 * Created by patny on 2017-08-14.
 */

public class MapRenderTimer {

    private long currentTimeNano;
    private long currentTimeMili;
    private long deltaNano;
    private long deltaMili;


    public void start() {
        currentTimeNano = System.nanoTime();
        currentTimeMili = System.currentTimeMillis();
    }

    public void update() {
        deltaNano = System.nanoTime() - currentTimeNano;
        deltaMili = System.currentTimeMillis() - currentTimeMili;
        currentTimeNano = System.nanoTime();
        currentTimeMili = System.currentTimeMillis();
    }

    public long getFrameTimeNano() {
        return deltaNano;
    }

    public long getFrameTimeMili() {
        return deltaMili;
    }


    //Do we need stop?
}
