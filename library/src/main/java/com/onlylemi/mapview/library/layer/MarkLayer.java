package com.onlylemi.mapview.library.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.BaseMark;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.graphics.implementation.ProximityMark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * MarkLayer
 *
 * @author: onlylemi
 */
public class MarkLayer extends MapBaseLayer {

    private MarkIsClickListener markClickListener;
    private MarkIsTriggered markTriggeredListener;

    private List<BaseMark> markObjects = new ArrayList();

    private List<ProximityMark> proxMarks = new ArrayList();

    /**
     * If you wanna use proximity triggers you need to include a reference to the user
     */
    private LocationUser user;

    private Paint paint;

    public MarkLayer(MapView mapView, LocationUser user) {
        this(mapView);

        this.user = user;
    }

    public MarkLayer(MapView mapView) {
        super(mapView);
        initLayer();
    }

    private void initLayer() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setStrokeWidth(0.2f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (markObjects != null && this.markClickListener != null) {
            if (!markObjects.isEmpty()) {
                //Log.d("MarkLayer", "Event x: " + event.getX() + ", y: " + event.getY());
                float[] goal = mapView.convertMapXYToScreenXY(event.getX(), event.getY());
                //Log.d("MarkLayer", "Goal x: " + goal[0] + ", y: " + goal[1]);
                for(int i = 0; i < markObjects.size(); i++) {
                    if(markObjects.get(i).getVisible() && markObjects.get(i).hit(new PointF(goal[0], goal[1]))) {
                        this.markClickListener.markIsClick(markObjects.get(i), i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {
        if (isVisible) {
            if (!markObjects.isEmpty()) {
                for (int i = 0; i < markObjects.size(); i++) {
                    BaseMark mark = markObjects.get(i);
                    mark.update(currentMatrix, deltaTime);
                    if(mark.getVisible()) {
                        mark.draw(canvas, paint);
                    }
                }
            }

            if(user != null && !proxMarks.isEmpty())
                checkTriggers();

        }
    }

    @Override
    public void debugDraw(Canvas canvas, Matrix currentMatrix) {
        if(isVisible) {
            canvas.save();
            if(!markObjects.isEmpty()) {
                for(BaseGraphics bg : markObjects) {
                    bg.debugDraw(currentMatrix, canvas);
                }
            }
            canvas.restore();
        }
    }

    private void checkTriggers() {
        for(int i = 0; i < proxMarks.size(); i++) {
            boolean inProx = proxMarks.get(i).triggerProximity(user.getPosition());
            if(inProx && !proxMarks.get(i).isTriggered()) {
                if(markTriggeredListener != null) {
                    proxMarks.get(i).setTriggered(true);
                    markTriggeredListener.onEnter(proxMarks.get(i), i);
                }
            } else if(!inProx && proxMarks.get(i).isTriggered()) { //This means we just exited
                    markTriggeredListener.onExit(proxMarks.get(i), i);

                    if(proxMarks.get(i).isOneTime()) {
                        this.markObjects.remove(proxMarks.get(i));
                        proxMarks.remove(i);
                    } else {
                        proxMarks.get(i).setTriggered(false);
                    }
            }
        }
    }

    public List<BaseMark> getMarks() {
        return markObjects;
    }

    public void setStaticMarks(List<? extends BaseMark> marks) {
        this.markObjects = new ArrayList<>(marks);
        this.markObjects.addAll(proxMarks);
    }

    public void setProximityMarks(final List<ProximityMark> proxMarks) {
        this.markObjects.removeAll(this.proxMarks);
        this.proxMarks = new ArrayList<>(proxMarks);
        this.markObjects.addAll(this.proxMarks);
    }

    public void setMarkIsClickListener(MarkIsClickListener listener) {
        this.markClickListener = listener;
    }

    public void setMarkTriggeredListener(MarkIsTriggered listener) {
        this.markTriggeredListener = listener;
    }

    public interface MarkIsClickListener {
        void markIsClick(BaseMark num, int index);
    }

    public interface MarkIsTriggered {
        void onEnter(ProximityMark mark, int index);
        void onExit(ProximityMark mark, int index);
    }
}
