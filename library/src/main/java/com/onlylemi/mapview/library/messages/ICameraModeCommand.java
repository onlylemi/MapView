package com.onlylemi.mapview.library.messages;

import com.onlylemi.mapview.library.camera.MapViewCamera;

/**
 * Created by patnym on 27/12/2017.
 */

@FunctionalInterface
public interface ICameraModeCommand {
    void execute(MapViewCamera camera);
}
