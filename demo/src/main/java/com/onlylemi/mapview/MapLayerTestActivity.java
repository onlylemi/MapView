package com.onlylemi.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewSetupHandler;
import com.onlylemi.mapview.library.MapViewSetupCallback;
import com.onlylemi.mapview.library.graphics.BaseMark;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.graphics.implementation.ProximityMark;
import com.onlylemi.mapview.library.graphics.implementation.StaticMark;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.utils.MapMath;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLayerTestActivity extends AppCompatActivity {

    private static final String TAG = "MapLayerTestActivity";

    private MapView mapView;

    private LocationUser user;
    private List<ProximityMark> marks = new ArrayList<>();

    private Matrix transformMatrix;
    private PointF position = new PointF(1.4f, 2.0f);

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

        try {
            // TODO: 2017-02-22 get from net
            //map = BitmapFactory.decodeStream(getAssets().open("map.png"));
            bg = BitmapFactory.decodeStream(getAssets().open("bg-coop.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapView.onSetupCallback(new MapViewSetupCallback() {
            @Override
            public void onSetup(MapViewSetupHandler handler) {
                try {
                    Bitmap map = BitmapFactory.decodeStream(getAssets().open("bromma_floor_plan810.png"));
                    handler.createMap(map);
                    transformMatrix = MapMath.createMappingMatrix(map, 5, 7, new PointF(0, 0), new PointF(map.getWidth(), map.getHeight()));   //<--------- THIS IS FOR THE BACKEND ROOM PNG
                    user = new LocationUser(BitmapFactory.decodeStream(getAssets().open("marker.png")), MapMath.transformPoint(transformMatrix, position), new PointF(1, 0));
                } catch(IOException ex) {
                    ex.printStackTrace();
                    return;
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

                markLayer.setMarkTriggeredListener(new MarkLayer.MarkIsTriggered() {
                    @Override
                    public void onEnter(ProximityMark mark, int index) {
                        Log.d(TAG, "Entered");
                    }

                    @Override
                    public void onExit(ProximityMark mark, int index) {
                        Log.d(TAG, "Exited");
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

                mapView.setDebug(true);


                //userHandler.moveUser(MapMath.transformPoint(transformMatrix, new PointF(1.4f, 2.0f)), 5.0f);
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
    boolean force = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        boolean handled = false;

        if (inited) {
            if (keyCode == KeyEvent.KEYCODE_W) {
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
                //position.y -= 0.5f;
                //user.setLookAt(new PointF(0.0f, 1.0f), 0.3f);
                //handled = true;
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
                mapView.pauseRendering();
            }

            if (keyCode == KeyEvent.KEYCODE_R) {
                force = !force;
                mapView.enableContinuousRendering(force);
            }

            if (keyCode == KeyEvent.KEYCODE_J) {
                mapView.resumeRendering();
            }

            if (keyCode == KeyEvent.KEYCODE_K) {
                marks.add(new ProximityMark(user.getBmp(), new PointF(user.getPosition().x, user.getPosition().y), user.getBmp().getWidth() * 2.0f, true, false));
                markHandler.setProximityMarks(marks);
            }

            if (keyCode == KeyEvent.KEYCODE_Q) {
                mapView.setCameraDefaultRevertDuration(1000);
            }

            if(keyCode == KeyEvent.KEYCODE_G) {
                mapView.setContainerUserMode();
            }

            if(keyCode == KeyEvent.KEYCODE_M) {
                View v = findViewById(R.id.root_view);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                params.width = 800;
                v.setLayoutParams(params);
            }

            if(keyCode == KeyEvent.KEYCODE_N) {
                View v = findViewById(R.id.root_view);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                v.setLayoutParams(params);
            }

            if(keyCode == KeyEvent.KEYCODE_T) {
                mapView.setFollowUserMode();
            }

            if(keyCode == KeyEvent.KEYCODE_E) {
                mapView.setFreeMode(Long.MAX_VALUE);
            }

            if (keyCode == KeyEvent.KEYCODE_P) {
                marks.add(new ProximityMark(user.getBmp(), MapMath.transformPoint(transformMatrix,
                        new PointF(0, 0)), user.getBmp().getWidth() * 2.0f,
                        true, false));
                marks.add(new ProximityMark(user.getBmp(), MapMath.transformPoint(transformMatrix,
                        new PointF(2, 0)), user.getBmp().getWidth() * 2.0f,
                        true, false));
                marks.add(new ProximityMark(user.getBmp(), MapMath.transformPoint(transformMatrix,
                        new PointF(2, 4)), user.getBmp().getWidth() * 2.0f,
                        true, false));
                marks.add(new ProximityMark(user.getBmp(), MapMath.transformPoint(transformMatrix,
                        new PointF(5, 10)), user.getBmp().getWidth() * 2.0f,
                        true, false));
                marks.add(new ProximityMark(user.getBmp(), MapMath.transformPoint(transformMatrix,
                        new PointF(-25, -10)), user.getBmp().getWidth() * 2.0f,
                        true, false));
                marks.add(new ProximityMark(user.getBmp(), MapMath.transformPoint(transformMatrix,
                        new PointF(3, 19)), user.getBmp().getWidth() * 2.0f,
                        true, false));
                markHandler.setProximityMarks(marks);
                mapView.setContainPointsMode(MapUtils.getPositionListFromGraphicList(marks), false, 500.0f);
            }

            //If continious is true it will keep the mark array as a reference
            //mapView.zoomWithinPoints(posis);
        }
        return handled;
    }

    //endregion


}
