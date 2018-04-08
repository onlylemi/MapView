package com.onlylemi.mapview.library;

public interface MapViewSetupCallback {

    /**
     * Gets called after the message handler has been setup. This allows the user to init and load graphics on a seperate thread
     * @param handler
     */
    void onSetup(MapViewSetupHandler handler);

    /**
     * This gets called once EVERYTHING is setup. Can now set camera mode ex.
     */
    void onPostSetup();
}
