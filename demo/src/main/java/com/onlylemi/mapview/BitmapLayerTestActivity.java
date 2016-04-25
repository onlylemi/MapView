package com.onlylemi.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;

import java.io.IOException;
import java.util.Random;

public class BitmapLayerTestActivity extends AppCompatActivity {


    private static final String TAG = "BitmapLayerTestActivity";

    private MapView mapView;
    private BitmapLayer bitmapLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_layer_test);

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

                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                bitmapLayer = new BitmapLayer(mapView, bmp);
                bitmapLayer.setLocation(new PointF(400, 400));
                bitmapLayer.setOnBitmapClickListener(new BitmapLayer.OnBitmapClickListener() {
                    @Override
                    public void onBitmapClick(BitmapLayer layer) {
                        Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
                    }
                });
                mapView.addLayer(bitmapLayer);
                mapView.refresh();
            }

            @Override
            public void onMapLoadFail() {
                Log.i(TAG, "onMapLoadFail");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bitmap_layer_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mapView.isMapLoadFinish()) {
            switch (item.getItemId()) {
                case R.id.bitmap_layer_set_position:
                    // change bmp postion
                    int x = new Random().nextInt((int) mapView.getMapWidth());
                    int y = new Random().nextInt((int) mapView.getMapHeight());
                    bitmapLayer.setLocation(new PointF(x, y));
                    mapView.refresh();
                    break;
                case R.id.bitmap_layer_set_mode:
                    // change bmp is/not scale
                    bitmapLayer.setAutoScale(!bitmapLayer.isAutoScale());
                    if (bitmapLayer.isAutoScale()) {
                        item.setTitle("Set Bitmap Not Scale");
                    } else {
                        item.setTitle("Set Bitmap Scale");
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
