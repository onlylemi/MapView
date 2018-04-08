package com.onlylemi.mapview.library.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewRenderer;
import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.BaseMark;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.graphics.implementation.ProximityMark;
import com.onlylemi.mapview.library.layer.handlers.BaseLayerHandler;
import com.onlylemi.mapview.library.messages.ICommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;

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

    private MarkHandler handler;

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
    public void onTouch(float x, float y) {
        if (markObjects != null && this.markClickListener != null) {
            if (!markObjects.isEmpty()) {
                //Log.d("MarkLayer", "Event x: " + event.getX() + ", y: " + event.getY());
                //Log.d("MarkLayer", "Goal x: " + goal[0] + ", y: " + goal[1]);
                float[] point = renderer.getCamera().convertMapXYToScreenXY(x, y);

                for(int i = 0; i < markObjects.size(); i++) {
                    if(markObjects.get(i).getVisible() && markObjects.get(i).hit(new PointF(point[0], point[1]))) {
                        this.markClickListener.markIsClick(markObjects.get(i), i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean update(Matrix currentMatrix, long deltaTime) {
        for(int i = 0; i < markObjects.size(); i++) {
            markObjects.get(i).update(currentMatrix, deltaTime);
        }

        if(user != null && !proxMarks.isEmpty())
            checkTriggers();

        return hasChanged;
    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {
        if (isVisible) {
            for (int i = 0; i < markObjects.size(); i++) {
                if(markObjects.get(i).getVisible()) {
                    markObjects.get(i).draw(canvas, paint);
                }
            }
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

    @Override
    public void createHandler(MapViewRenderer renderThread) {
        this.handler = new MarkHandler(renderThread.getHandler(), this);
        super.createHandler(renderThread);
    }

    public MarkHandler getMarkHandler() {
        return handler;
    }

    public interface MarkIsClickListener {
        void markIsClick(BaseMark num, int index);
    }

    public interface MarkIsTriggered {
        void onEnter(ProximityMark mark, int index);
        void onExit(ProximityMark mark, int index);
    }

    public class MarkHandler extends BaseLayerHandler {
        private Handler renderHandler;
        private MarkLayer markLayer;

        public MarkHandler(Handler renderHandler, MarkLayer markLayer) {
            super(renderHandler, markLayer);
            this.renderHandler = renderHandler;
            this.markLayer = markLayer;
        }

        public void setStaticMarks(final List<? extends BaseMark> marks) {
            runOnRenderThread(new ICommand() {
                @Override
                public void execute() {
                    markLayer.setStaticMarks(marks);
                }
            });
        }

        public void setProximityMarks(final List<ProximityMark> proxMarks) {
            runOnRenderThread(new ICommand() {
                @Override
                public void execute() {
                    markLayer.setProximityMarks(proxMarks);
                }
            });
        }
    }
}
