package com.onlylemi.mapview.library;

import android.graphics.Bitmap;

import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.layer.MapLayer;

/**
 * Created by patnym on 26/12/2017.
 */

public class MapViewHandler {

    private MapView view;
    private MapViewRenderer renderer;

    private LocationUser currentUser;

    public MapViewHandler(MapView view, MapViewRenderer renderer) {
        this.view = view;
        this.renderer = renderer;
    }

    /**
     * Creates a map from a bitmap
     * @param bmp
     */
    public void createMap(Bitmap bmp) {
        MapLayer mapLayer = new MapLayer(view);
        mapLayer.setBmp(bmp);
        addLayer(mapLayer);
        renderer.setMapLayer(mapLayer);
    }

    /**
     * Creates a empty canvas map
     * @param width
     * @param height
     */
    public void createMap(int width, int height) {

    }

    public void setTrackedUser(LocationUser user) {
        currentUser = user;
    }

    public void addLayer(MapBaseLayer layer) {
        layer.createHandler(renderer);
        renderer.addLayer(layer);
    }

    public LocationUser getUser() {
        return currentUser;
    }
}
