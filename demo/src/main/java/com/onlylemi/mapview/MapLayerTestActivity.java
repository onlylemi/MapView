package com.onlylemi.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;

import java.io.IOException;
import java.util.Random;

public class MapLayerTestActivity extends AppCompatActivity {

    private static final String TAG = "MapLayerTestActivity";

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layer_test);

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
                Log.i(TAG, "onMapLoadSuccess");
                //mapView.setCurrentRotateDegrees(60);
            }

            @Override
            public void onMapLoadFail() {
                Log.i(TAG, "onMapLoadFail");
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_layer_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mapView.isMapLoadFinish()) {
            switch (item.getItemId()) {
                case R.id.map_layer_set_rotate:
                    int rotate = new Random().nextInt(360);
                    mapView.setCurrentRotateDegrees(rotate);
                    mapView.refresh();

                    Toast.makeText(this, "current rotate: " + rotate, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.map_layer_set_zoom1:
                    mapView.setCurrentZoom(mapView.getCurrentZoom() / 2);
                    mapView.refresh();
                    break;
                case R.id.map_layer_set_zoom2:
                    mapView.setCurrentZoom(mapView.getCurrentZoom() * 2);
                    mapView.refresh();
                    break;
                case R.id.map_layer_set_auto_rotate_and_scale:
                    if (mapView.isScaleAndRotateTogether()) {
                        item.setTitle("Set Rotate and Scale Together");
                    } else {
                        item.setTitle("Set Rotate and Scale Not Together");
                    }
                    mapView.setScaleAndRotateTogether(!mapView.isScaleAndRotateTogether());
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
