package com.onlylemi.mapview.library.layer.handlers;

import android.os.Handler;

import com.onlylemi.mapview.library.MapViewRenderer;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.messages.ICommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;

/**
 * Created by patnym on 2018-04-02.
 */

public class BaseLayerHandler {

    protected Handler renderer;

    protected MapBaseLayer layer;

    public BaseLayerHandler(Handler renderer, MapBaseLayer layer) {
        this.layer = layer;
        this.renderer = renderer;
    }

    /**
     * Should never directly get called only from handler functions
     * @param command
     */
    public void runOnRenderThread(ICommand command) {
        MessageDefenitions.sendExecuteMessage(renderer,
                MessageDefenitions.MESSAGE_EXECUTE, command);
        layer.triggerChange();
    }
}
