package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.graphics.IMark;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * MarkLayer
 *
 * @author: onlylemi
 */
public class MarkLayer extends MapBaseLayer {

    private List<PointF> marks;
    private List<String> marksName;
    private MarkIsClickListener listener;

    private List<IMark> markObjects;

    private Bitmap bmpMark, bmpMarkTouch;

    private Paint paint;

    public MarkLayer(MapView mapView) {
        this(mapView, new ArrayList<PointF>(), new ArrayList<String>());
}

    public MarkLayer(MapView mapView, List<PointF> marks, List<String> marksName) {
        super(mapView);
        this.marks = marks;
        this.marksName = marksName;

        bmpMark = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.mark);
        bmpMarkTouch = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.mark_touch);

        initLayer();
    }

    private void initLayer() {
        radiusMark = setValue(10f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (marks != null) {
            if (!marks.isEmpty()) {
                float[] goal = mapView.convertMapXYToScreenXY(event.getX(), event.getY());
                for (int i = 0; i < marks.size(); i++) {
                    //TODO(Nyman): This is hardcoded collision detection, fix it!
                    if (MapMath.getDistanceBetweenTwoPoints(goal[0], goal[1],
                            marks.get(i).x - bmpMark.getWidth() / 2, marks.get(i).y - bmpMark
                                    .getHeight() / 2) <= 50) {
                        num = i;
                        isClickMark = true;
                        break;
                    }

                    if (i == marks.size() - 1) {
                        isClickMark = false;
                    }
                }
            }

            if (listener != null && isClickMark) {
                listener.markIsClick(num);
                mapView.refresh();
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        if (isVisible && markObjects != null) {
            canvas.save();
            if (!markObjects.isEmpty()) {
                for (int i = 0; i < markObjects.size(); i++) {
                    IMark mark = markObjects.get(i);
                    mark.update(currentMatrix);
                    mark.draw(canvas, paint);
                }
            }
            canvas.restore();
        }
    }

    public List<IMark> getMarks() {
        return markObjects;
    }

    public void setMarks(List<IMark> marks) {
        this.markObjects = marks;
    }

    public void setMarksName(List<String> marksName) {
        this.marksName = marksName;
    }

    public boolean isClickMark() {
        return isClickMark;
    }

    public void setMarkIsClickListener(MarkIsClickListener listener) {
        this.listener = listener;
    }

    //TODO(Nyman): Send mark object instead
    public interface MarkIsClickListener {
        void markIsClick(int num);
    }
}
