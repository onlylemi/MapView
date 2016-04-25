package com.onlylemi.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.utils.MapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouteLayerTestActivity extends AppCompatActivity {

    private MapView mapView;

    private MarkLayer markLayer;
    private RouteLayer routeLayer;

    private List<PointF> nodes;
    private List<PointF> nodesContract;
    private List<PointF> marks;
    private List<String> marksName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_layer_test);

        nodes = TestData.getNodesList();
        nodesContract = TestData.getNodesContactList();
        marks = TestData.getMarks();
        marksName = TestData.getMarksName();
        MapUtils.init(nodes.size(), nodesContract.size());

        mapView = (MapView) findViewById(R.id.mapview);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapView.loadMap(bitmap);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                routeLayer = new RouteLayer(mapView);
                mapView.addLayer(routeLayer);

                markLayer = new MarkLayer(mapView, marks, marksName);
                mapView.addLayer(markLayer);
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num) {
                        PointF target = new PointF(marks.get(num).x, marks.get(num).y);
                        List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints
                                (marks.get(39), target, nodes, nodesContract);
                        routeLayer.setNodeList(nodes);
                        routeLayer.setRouteList(routeList);
                        mapView.refresh();
                    }
                });
                mapView.refresh();
            }

            @Override
            public void onMapLoadFail() {
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route_layer_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mapView.isMapLoadFinish()) {
            switch (item.getItemId()) {
                case R.id.route_layer_tsp:
                    List<PointF> list = new ArrayList<>();
                    list.add(marks.get(39));
                    list.add(marks.get(new Random().nextInt(10)));
                    list.add(marks.get(new Random().nextInt(10) + 10));
                    list.add(marks.get(new Random().nextInt(10) + 20));
                    list.add(marks.get(new Random().nextInt(10) + 9));
                    List<Integer> routeList = MapUtils.getBestPathBetweenPoints(list, nodes,
                            nodesContract);
                    routeLayer.setNodeList(nodes);
                    routeLayer.setRouteList(routeList);
                    mapView.refresh();
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
