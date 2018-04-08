package com.onlylemi.mapview.library.messages;

import android.view.MotionEvent;

/**
 * Created by patnym on 2018-03-20.
 */

public class MotionEventMessage {

    private final float[] x;
    private final float[] y;
    private final int pointerCount;
    private final int action;

    public MotionEventMessage(float x, float y, int action) {
        this(new float[] { x }, new float[]{ y }, action, 1);
    }

    public MotionEventMessage(float[] x, float[] y, int action, int pointerCount) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.pointerCount = pointerCount;
    }

    public int getPointerCount() {
        return pointerCount;
    }

    public int getAction() {
        return action;
    }

    public float getX() {
        return getX(0);
    }

    public float getX(int index) {
        return x[index];
    }

    public float getY() {
        return getY(0);
    }

    public float getY(int index) {
        return y[index];
    }

    /**
     * Converts a MotionEvent message into a MotionEventMessage that can be sent to the RenderThread
     * @param event
     * @return
     */
    public static MotionEventMessage MessageFromMotionEvent(MotionEvent event) {
        if(event.getPointerCount() > 1) {
            return new MotionEventMessage(new float[]{ event.getX(0), event.getX(1) },
                    new float[]{ event.getY(0), event.getY(1) },
                    event.getAction(), event.getPointerCount());
        } else {
            return new MotionEventMessage(event.getX(), event.getY(), event.getAction());
        }
    }

}
