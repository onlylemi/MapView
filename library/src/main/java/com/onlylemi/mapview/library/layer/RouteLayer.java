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
import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * RouteLayer
 *
 * @author: onlylemi
 */
//// TODO: 2018-03-31 Rewrite this using Path object
@Deprecated
public class RouteLayer extends MapBaseLayer {

    private List<PointF> routeList; // routes list
    private BaseGraphics user; //Reference to the user graphics

    private float routeWidth;

    private Paint paint;

    public RouteLayer(MapView mapView) {
        super(mapView);
        initLayer();
    }

    public RouteLayer(MapView mapView, LocationUser user) {
        this(mapView);
        this.user = user;
    }

    private void initLayer() {
//        this.routeWidth = mapView.getMapModeOptions().routeLineWidth;
//
//        routeList = Collections.emptyList();
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setColor(mapView.getMapModeOptions().routeLineColor);
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void onTouch(float x, float y) {

    }

    @Override
    public boolean update(Matrix currentMatrix, long deltaTime) {
        return false;
    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {

//        if(!routeList.isEmpty()) {
//
//            float thickness = currentMatrix.mapRadius(routeWidth);
//            paint.setStrokeWidth(thickness);
//            //We go backwards as to add the user position to the last line
//            for(int i = routeList.size() - 1; i > 0; i--) {
//                float[] start = { routeList.get(i).x, routeList.get(i).y };
//                float[] end = { routeList.get(i - 1).x, routeList.get(i - 1).y };
//                currentMatrix.mapPoints(start);
//                currentMatrix.mapPoints(end);
//                canvas.drawLine(start[0], start[1], end[0], end[1], paint);
//            }
//
//            if(user != null) {
//                float[] start = { routeList.get(0).x, routeList.get(0).y };
//                float[] end = { user.position.x, user.position.y };
//                currentMatrix.mapPoints(start);
//                currentMatrix.mapPoints(end);
//                canvas.drawLine(start[0], start[1], end[0], end[1], paint);
//            }
//
//        }
    }

    @Override
    public void debugDraw(Canvas canvas, Matrix currentMatrix) {

    }

    public void setRouteList(List<PointF> routeList) {
        this.routeList = routeList;
    }
}
