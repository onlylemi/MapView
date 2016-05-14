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
import com.onlylemi.mapview.library.R;

import java.util.List;

/**
 * RouteLayer
 *
 * @author: onlylemi
 */
public class RouteLayer extends MapBaseLayer {

    private List<Integer> routeList; // routes list
    private List<PointF> nodeList; // nodes list

    private float routeWidth; // the width of route

    private Bitmap routeStartBmp;
    private Bitmap routeEndBmp;

    private Paint paint;

    public RouteLayer(MapView mapView) {
        this(mapView, null, null);
    }

    public RouteLayer(MapView mapView, List<PointF> nodeList, List<Integer> routeList) {
        super(mapView);
        this.nodeList = nodeList;
        this.routeList = routeList;

        initLayer();
    }

    private void initLayer() {
        this.routeWidth = 10;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        routeStartBmp = BitmapFactory.decodeResource(mapView.getResources(),
                R.mipmap.start_point);
        routeEndBmp = BitmapFactory.decodeResource(mapView.getResources(),
                R.mipmap.end_point);
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, float
            currentRotateDegrees) {
        if (isVisible && routeList != null && nodeList != null) {
            canvas.save();
            if (!routeList.isEmpty() && !nodeList.isEmpty()) {
                // draw route
                for (int i = 0; i < routeList.size() - 1; i++) {
                    float[] goal1 = {nodeList.get(routeList.get(i)).x,
                            nodeList.get(routeList.get(i)).y};
                    float[] goal2 = {nodeList.get(routeList.get(i + 1)).x,
                            nodeList.get(routeList.get(i + 1)).y};
                    currentMatrix.mapPoints(goal1);
                    currentMatrix.mapPoints(goal2);
                    paint.setStrokeWidth(routeWidth);
                    canvas.drawLine(goal1[0], goal1[1], goal2[0], goal2[1], paint);
                }

                // draw bmp
                float[] goal1 = {nodeList.get(routeList.get(0)).x,
                        nodeList.get(routeList.get(0)).y};
                float[]  goal2 = {
                        nodeList.get(routeList.get(routeList.size() - 1)).x,
                        nodeList.get(routeList.get(routeList.size() - 1)).y};
                currentMatrix.mapPoints(goal1);
                currentMatrix.mapPoints(goal2);
                canvas.drawBitmap(routeStartBmp,
                        goal1[0] - routeStartBmp.getWidth() / 2, goal1[1]
                                - routeStartBmp.getHeight(), paint);
                canvas.drawBitmap(routeEndBmp,
                        goal2[0] - routeEndBmp.getWidth() / 2, goal2[1]
                                - routeEndBmp.getHeight(), paint);
            }
            canvas.restore();
        }
    }

    public void setNodeList(List<PointF> nodeList) {
        this.nodeList = nodeList;
    }

    public void setRouteList(List<Integer> routeList) {
        this.routeList = routeList;
    }
}
