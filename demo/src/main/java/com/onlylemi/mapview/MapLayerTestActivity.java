package com.onlylemi.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.graphics.BaseGraphics;
import com.onlylemi.mapview.library.graphics.BaseMark;
import com.onlylemi.mapview.library.graphics.implementation.Backgrounds.ColorBackground;
import com.onlylemi.mapview.library.graphics.implementation.Backgrounds.TiledBitmapBackground;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.graphics.implementation.ProximityMark;
import com.onlylemi.mapview.library.graphics.implementation.StaticMark;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapModeOptions;

import junit.framework.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLayerTestActivity extends AppCompatActivity {

    private static final String TAG = "MapLayerTestActivity";

    private MapView mapView;

    private LocationUser user;

    private Matrix transformMatrix;
    private PointF position = new PointF(0, 0);

    private LocationLayer locationLayer;
    private MarkLayer markLayer;
    private RouteLayer routeLayer;
    private Bitmap bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layer_test);

        mapView = (MapView) findViewById(R.id.mapview);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRating = display.getRefreshRate();

        Log.d(TAG, "Refreshrate is: " + refreshRating);

        try {
            // TODO: 2017-02-22 get from net
            Bitmap map = BitmapFactory.decodeStream(getAssets().open("bromma_floor_plan810.png"));
            bg = BitmapFactory.decodeStream(getAssets().open("bg-coop.png"));
            transformMatrix = MapMath.createMappingMatrix(map, 7, 5, new PointF(0, 0), new PointF(633, 500));   //<--------- THIS IS FOR THE BACKEND ROOM PNG

            try {
                user = new LocationUser(BitmapFactory.decodeStream(getAssets().open("marker.png")), MapMath.transformPoint(transformMatrix, position), new PointF(1, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }

            mapView.setUser(user);

            mapView.loadMap(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                locationLayer = new LocationLayer(mapView, user);
                routeLayer = new RouteLayer(mapView, user);
                markLayer = new MarkLayer(mapView, user);

                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(BaseMark iMark, int i) {
                        //Handle click. You get a reference to the IMark and the index in the mark array
                        Log.d(TAG, "Clicked a mark");
                    }
                });

                markLayer.setMarkTriggeredListener(new MarkLayer.MarkIsTriggered() {
                    @Override
                    public void onEnter(ProximityMark mark, int index) {

                        mark.setVisible(true);

                        Log.d(TAG, "Trigered mark");
                    }

                    @Override
                    public void onExit(ProximityMark mark, int index) {
                        Log.d(TAG, "Exited mark");

                        mark.setVisible(false);

                    }
                });

                mapView.addLayer(locationLayer);
                mapView.addLayer(markLayer);
                mapView.addLayer(routeLayer);
                mapView.setDebug(true);
            }

            @Override
            public void onMapLoadFail() {
                Log.e(TAG, "Failed to load map");
            }

            @Override
            public void onRenderingStarted(int viewWidth, int viewHeight) {
                mapView.setBackground(new TiledBitmapBackground(getResources(), bg, viewWidth, viewHeight, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));

                //mapView.setFixedFPS(15);
            }
        });

    }

    //region DEBUGGING

    //Enable or disable freelooking
    private boolean freeLook = false;

    private boolean debug = false;

    private int index = 0;

    private ArrayList<ProximityMark> bs = new ArrayList();
    private ArrayList<StaticMark> sm = new ArrayList();
    private ArrayList<PointF> route = new ArrayList<>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        boolean handled = false;

        if (mapView.isMapLoadFinish()) {
            if (keyCode == KeyEvent.KEYCODE_W) {
                position.y -= 0.5f;
                user.setLookAt(new PointF(0.0f, 1.0f), 0.3f);
                handled = true;
            } else if (keyCode == KeyEvent.KEYCODE_A) {
                position.x -= 0.5f;
                user.setLookAt(new PointF(-1, 0), 0.3f);
                handled = true;
            } else if (keyCode == KeyEvent.KEYCODE_S) {
                position.y += 0.5f;
                user.setLookAt(new PointF(0.0f, -1.0f), 0.3f);
                handled = true;
            } else if (keyCode == KeyEvent.KEYCODE_D) {
                position.x += 0.5f;
                user.setLookAt(new PointF(1, 0), 0.3f);
                handled = true;
            }

            if (keyCode == KeyEvent.KEYCODE_H) {
                debug = !debug;
                mapView.setDebug(debug);
            }

            if (keyCode == KeyEvent.KEYCODE_K) {

//                View v = findViewById(R.id.mappi);
//                v.setVisibility(View.GONE);
                    mapView.pauseRendering();
//                markLayer.setStaticMarks(new ArrayList<BaseMark>());
//                markLayer.setProximityMarks(new ArrayList<ProximityMark>());

            }

            if(keyCode == KeyEvent.KEYCODE_G) {
                mapView.disableFixedFPS();
            }

            if(keyCode == KeyEvent.KEYCODE_T) {
                mapView.setFixedFPS(5);
            }

            if (keyCode == KeyEvent.KEYCODE_P) {
//                View v = findViewById(R.id.mappi);
//                v.setVisibility(View.VISIBLE);
                mapView.resumeRendering();
//                try {
//                    Bitmap markBm = BitmapFactory.decodeStream(getAssets().open("marker.png"));
//
//                    bs.add(new ProximityMark(markBm, TestData.getMarks().get(index), markBm.getHeight() * 2, true, false));
//                    sm.add(new StaticMark(markBm, TestData.getMarks().get(index)));
//                    markLayer.setProximityMarks(bs);
//                    markLayer.setStaticMarks(sm);
//                    route.add(TestData.getMarks().get(index));
//                    routeLayer.setRouteList(route);
//
//                    index++;
//
//                    mapView.setZoomPoints(bs, 5.0f, true);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

            //If continious is true it will keep the mark array as a reference
            //mapView.zoomWithinPoints(posis);

            user.move(MapMath.transformPoint(transformMatrix, position), 0.1f);
        }
        return handled;
    }

    //endregion


}
