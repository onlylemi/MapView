package com.onlylemi.mapview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView maplayerListView;
    private ArrayAdapter<String> mAdapter;

    private Class[] classes = {MapLayerTestActivity.class, BitmapLayerTestActivity.class,
            LocationLayerTestActivity.class, MarkLayerTestActivity.class, RouteLayerTestActivity
            .class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maplayerListView = (ListView) findViewById(R.id.mapview_lv);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.maplayer_name));
        maplayerListView.setAdapter(mAdapter);
        maplayerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, classes[position].getSimpleName());
                startActivity(new Intent(MainActivity.this, classes[position]));
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
