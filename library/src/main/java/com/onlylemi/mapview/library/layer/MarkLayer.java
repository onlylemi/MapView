package com.onlylemi.mapview.library.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
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

    private MarkIsClickListener listener;

    private List<IMark> markObjects;

    private Paint paint;

    public MarkLayer(MapView mapView) {
        super(mapView);
        initLayer();
    }

    private void initLayer() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (markObjects != null && this.listener != null) {
            if (!markObjects.isEmpty()) {
                Log.d("MarkLayer", "Event x: " + event.getX() + ", y: " + event.getY());
                float[] goal = mapView.convertMapXYToScreenXY(event.getX(), event.getY());
                Log.d("MarkLayer", "Goal x: " + goal[0] + ", y: " + goal[1]);
                for(int i = 0; i < markObjects.size(); i++) {
                    if(markObjects.get(i).hit(new PointF(goal[0], goal[1]))) {
                        this.listener.markIsClick(markObjects.get(i), i);
                        break;
                    }
                }
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

    public void setMarkIsClickListener(MarkIsClickListener listener) {
        this.listener = listener;
    }

    public interface MarkIsClickListener {
        void markIsClick(IMark num, int index);
    }
}
