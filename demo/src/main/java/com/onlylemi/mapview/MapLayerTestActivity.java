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
import com.onlylemi.mapview.library.MapViewHandler;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.MapViewSetupCallback;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapLayerTestActivity extends AppCompatActivity {

    private static final String TAG = "MapLayerTestActivity";

    private MapView mapView;

    private LocationUser user;
    private List<ProximityMark> marks = new ArrayList<>();

    private Matrix transformMatrix;
    private PointF position = new PointF(0, 0);

    private LocationLayer.UserHandler userHandler;
    private MarkLayer.MarkHandler markHandler;

    private LocationLayer locationLayer;
    private MarkLayer markLayer;
    private RouteLayer routeLayer;
    private Bitmap bg;

    private boolean inited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layer_test);

        mapView = (MapView) findViewById(R.id.mapview);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRating = display.getRefreshRate();

        Log.d(TAG, "Refreshrate is: " + refreshRating);
        Bitmap map = null;
        try {
            // TODO: 2017-02-22 get from net
            map = BitmapFactory.decodeStream(getAssets().open("map.png"));
            bg = BitmapFactory.decodeStream(getAssets().open("bg-coop.png"));
            transformMatrix = MapMath.createMappingMatrix(map, 5, 7, new PointF(0, 0), new PointF(2170, 861));   //<--------- THIS IS FOR THE BACKEND ROOM PNG

            try {
                user = new LocationUser(BitmapFactory.decodeStream(getAssets().open("marker.png")), MapMath.transformPoint(transformMatrix, position), new PointF(1, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapView.onSetupCallback(new MapViewSetupCallback() {
            @Override
            public void onSetup(MapViewHandler handler) {
                try {
                    handler.createMap(BitmapFactory.decodeStream(getAssets().open("bromma_floor_plan810_rotated.png")));
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
                LocationLayer locationLayer = new LocationLayer(mapView, user);
                handler.addLayer(locationLayer);

                MarkLayer markLayer = new MarkLayer(mapView, user);
                handler.addLayer(markLayer);

                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(BaseMark num, int index) {
                        Log.d(TAG, "Clicked mark");
                    }
                });

                handler.setTrackedUser(user);

                userHandler = locationLayer.getUserHandler();
                markHandler = markLayer.getMarkHandler();
            }

            @Override
            public void onPostSetup() {
                Log.d(TAG, "Everything has inited, now I can do whatever I want");

                inited = true;

                mapView.setContainerUserMode();
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

    boolean b = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        boolean handled = false;

        if (inited) {
            if (keyCode == KeyEvent.KEYCODE_W) {
                position.y -= 0.5f;
                //user.setLookAt(new PointF(0.0f, 1.0f), 0.3f);
                handled = true;
            } else if (keyCode == KeyEvent.KEYCODE_A) {
                position.x -= 0.5f;
                //user.setLookAt(new PointF(-1, 0), 0.3f);
                handled = true;
            } else if (keyCode == KeyEvent.KEYCODE_S) {
                position.y += 0.5f;
                //user.setLookAt(new PointF(0.0f, -1.0f), 0.3f);
                handled = true;
            } else if (keyCode == KeyEvent.KEYCODE_D) {
                position.x += 0.5f;
                //user.setLookAt(new PointF(1, 0), 0.3f);
                handled = true;
            }

            if (keyCode == KeyEvent.KEYCODE_H) {
                mapView.setFreeMode();
            }

            if (keyCode == KeyEvent.KEYCODE_K) {
                marks.add(new ProximityMark(user.getBmp(), new PointF(user.getPosition().x, user.getPosition().y), user.getBmp().getWidth() * 2.0f, true, false));
                markHandler.setProximityMarks(marks);
            }

            if(keyCode == KeyEvent.KEYCODE_G) {
                mapView.setContainerUserMode();
            }

            if(keyCode == KeyEvent.KEYCODE_T) {
                mapView.setFollowUserMode();
            }

            if (keyCode == KeyEvent.KEYCODE_P) {
                mapView.setContainGraphicsPointsMode(marks, true);
//                View v = findViewById(R.id.mappi);
//                v.setVisibility(View.VISIBLE);
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
            if(b) {
                List<PointF> positions = new ArrayList<>();

                positions.add(MapMath.transformPoint(transformMatrix, new PointF(5, 0)));
                positions.add(MapMath.transformPoint(transformMatrix, new PointF(5, 7)));
                positions.add(MapMath.transformPoint(transformMatrix, new PointF(0, 7)));
                positions.add(MapMath.transformPoint(transformMatrix, new PointF(0, 0)));

                userHandler.moveUser(positions, 5.0f, true);
            } else {
                userHandler.moveUser(MapMath.transformPoint(transformMatrix, new PointF(5, 7)), 1.0f);
            }
        }
        return handled;
    }

    //endregion


}
