package com.onlylemi.mapview.library.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewRenderer;
import com.onlylemi.mapview.library.graphics.implementation.LocationUser;
import com.onlylemi.mapview.library.messages.ICommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;

import java.util.List;

/**
 * LocationLayer
 *
 * @author: onlylemi
 */
public class LocationLayer extends MapBaseLayer {
    private static final String TAG ="LocationLayer";

    //user
    private LocationUser user;

    private Paint locationPaint;

    //Outside usage use this handler to interact with the user
    private UserHandler handler;

    public LocationLayer(MapView mapView, LocationUser user) {
        super(mapView);
        this.user = user;
        initLayer();
    }

    private void initLayer() {
        locationPaint = new Paint();
        locationPaint.setAntiAlias(true);
        locationPaint.setFilterBitmap(true);
        locationPaint.setDither(true);
    }

    @Override
    public void onTouch(MotionEvent event) {

    }

    @Override
    public void draw(Canvas canvas, Matrix currentMatrix, float currentZoom, long deltaTime) {
        //Later I wanna handle movement directions and shit in this layer
        if (isVisible) {
            user.update(currentMatrix, deltaTime);
            user.draw(canvas, locationPaint);
        }
    }

    @Override
    public void debugDraw(Canvas canvas, Matrix currentMatrix) {
        if(isVisible) {
            user.debugDraw(currentMatrix, canvas);
        }
    }

    /**
     * This gets called from the mapview once rendering is initiated. DO ATTEMPT TO CREATE YOUR OWN
     * @param renderThread
     */
    @Override
    public void createHandler(@NonNull MapViewRenderer renderThread) {
        this.handler = new UserHandler(renderThread.getHandler(), user);
    }

    public UserHandler getUserHandler() {
        if(this.handler == null) {
            throw new RuntimeException("Can't get handler until the rendering has started" +
                    ". Must be called inside or after the \" On rendering started\" has occurred");
        }
        return this.handler;
    }

    public class UserHandler {

        private Handler renderHandler;
        private LocationUser user;

        public UserHandler(Handler renderHandler, LocationUser user) {
            this.user = user;
            this.renderHandler = renderHandler;
        }

        public void moveUser(final List<PointF> destinationsQueue,final float duration,final boolean appendToOldList) {
            MessageDefenitions.sendExecuteMessage(renderHandler, MessageDefenitions.MESSAGE_EXECUTE, new ICommand() {
                @Override
                public void execute() {
                    user.move(destinationsQueue, duration, appendToOldList);
                }
            });
        }

        public void moveUser(final PointF position,final float duration) {
            MessageDefenitions.sendExecuteMessage(renderHandler, MessageDefenitions.MESSAGE_EXECUTE, new ICommand() {
                @Override
                public void execute() {
                    user.move(position, duration);
                }
            });
        }

    }
}
