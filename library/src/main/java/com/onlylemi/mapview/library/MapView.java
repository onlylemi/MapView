package com.onlylemi.mapview.library;

import android.content.Context;
import android.graphics.PointF;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.onlylemi.mapview.library.camera.MapViewCamera;
import com.onlylemi.mapview.library.layer.EmptyMapLayer;
import com.onlylemi.mapview.library.layer.MapBaseLayer;
import com.onlylemi.mapview.library.layer.MapLayer;
import com.onlylemi.mapview.library.messages.ICameraModeCommand;
import com.onlylemi.mapview.library.messages.MessageDefenitions;
import com.onlylemi.mapview.library.messages.MotionEventMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * MapView
 *
 * @author: onlylemi
 */
public class MapView extends SurfaceView implements SurfaceHolder.Callback, Choreographer.FrameCallback {

    private static final String TAG = "MapView";

    private MapViewListener mapViewListener = null;
    private MapViewSetupCallback setupCallback = null;
    private List<MapBaseLayer> layers; // all layers
    private MapLayer mapLayer;

    //Main rendering thread
    private MapViewRenderer thread; // = new MapViewRenderer();

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMapView();
    }

    /**
     * init mapview
     */
    private void initMapView() {
        getHolder().addCallback(this);

        layers = new ArrayList<MapBaseLayer>() {
            @Override
            public boolean add(MapBaseLayer layer) {
                if (layers.size() != 0) {
                    if (layer.level >= this.get(this.size() - 1).level) {
                        super.add(layer);
                    } else {
                        for (int i = 0; i < layers.size(); i++) {
                            if (layer.level < this.get(i).level) {
                                super.add(i, layer);
                                break;
                            }
                        }
                    }
                } else {
                    super.add(layer);
                }
                return true;
            }
        };

    }

    /**
     * Suspends teh rendering thread
     */
    public void resumeRendering() {
        if(thread != null) {
            thread.wakeUp();
        }
    }

    /**
     * Resumes the rendering thread
     */
    public void pauseRendering() {
        if(thread != null) {
            thread.pause();
        }
    }

    @Override
    public void onVisibilityChanged(View changedView, int state) {
        super.onVisibilityChanged(changedView, state);

        if ((state == View.GONE || state == View.INVISIBLE)) {
            pauseRendering();
        }else if(state == View.VISIBLE) {
            resumeRendering();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (thread == null || thread.getState() == Thread.State.TERMINATED){
            Log.d(TAG, "Creating a new render thread");
            thread = new MapViewRenderer(holder, this);
            thread.setSetupCallback(setupCallback);
            thread.start();
        }
        else if(thread.getState() == Thread.State.NEW){
            Log.d(TAG, "Using an old thread");
            thread.init(holder, this);
            thread.setSetupCallback(setupCallback);
            thread.start();
        }
        Log.d(TAG, "Surface created, size to: " + getWidth() + "x" + getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface resized to: " + width + "x"+height);
        if(thread != null && thread.getHandler() != null) {
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(),
                    MessageDefenitions.MESSAGE_SURFACE_CHANGED, width, height));
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is getting destroyed");
        if(thread != null) {
            //Resume thread execution
            Choreographer.getInstance().removeFrameCallback(this);
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(),
                    MessageDefenitions.MESSAGE_EXIT_THREAD));
            try {
                thread.join();
            }catch (InterruptedException ie) {
                ie.printStackTrace();
            }finally {
                //Let thread finish and exit
                Log.d(TAG, "Rendering thread terminated");
            }
        }
    }

    public void onSetupCallback(MapViewSetupCallback callback) {
        this.setupCallback = callback;
        if(this.thread != null) {
            this.thread.setSetupCallback(callback);
        }
    }

    /**
     * This creates an empty canavas to draw your own map on
     * Will not trigger the failed maploading listener as we are not loading anything
     * Triggers the loadSuccess to for consistency
     * @param width
     * @param height
     */
    // TODO: 2018-03-31 Fix this to work with the new initialization
    @Deprecated
    public void createMap(int width, int height) {
        if(mapLayer == null) {
            mapLayer = new EmptyMapLayer(this, width, height);
            layers.add(mapLayer);
        }
        if(mapViewListener != null) {
            mapViewListener.onMapLoadSuccess();
        }
        //isMapLoadFinish = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(thread != null && thread.getHandler() != null) {
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(),
                    MessageDefenitions.MESSAGE_MOTIONEVENT, MotionEventMessage.MessageFromMotionEvent(event)));
        }
        return true;
    }

    @Override
    public void doFrame(long deltaTimeNano) {
        if(thread != null && thread.getHandler() != null)
            thread.getHandler().sendMessage(Message.obtain(thread.getHandler(), MessageDefenitions.MESSAGE_DRAW, (int) (deltaTimeNano >> 32), (int) deltaTimeNano));
    }

    public void setContainerUserMode() {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createContainUserMode());
            }
        });
    }

    public void setFreeMode() {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFreeMode());
            }
        });
    }

    public void setFreeMode(final long durationNano) {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFreeMode(durationNano));
            }
        });
    }

    public void setFollowUserMode() {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFollowUserMode());
            }
        });
    }

    public void setFollowUserMode(final float zoomLevel) {
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createFollowUserMode(zoomLevel));
            }
        });
    }

    public void setContainPointsMode(final List<PointF> points, final boolean includeUser, final float padding) throws IllegalArgumentException {
        if(includeUser && points.size() < 1)
            throw new IllegalArgumentException("Zoom points size < 1, must include at least 1 point when including a user");
        else if(!includeUser && points.size() <= 1)
            throw new IllegalArgumentException("Zoom points size is less or equals to 1, must be > 1 if no user is included");
        else if(padding < 0.0f) {
            throw new IllegalArgumentException("Padding must be greater then 0, please choose narrower points instead");
        }
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.switchCameraMode(camera.getFactory().createContainPointsMode(points, includeUser, padding));
            }
        });
    }

    public void enableContinuousRendering(boolean enable) {
        thread.forceContinousRendering = enable;
    }

    public void setCameraDefaultRevertDuration(float durationMS) {
        final long durationNano = (long) durationMS * 1000000;
        sendCameraMessageToThread(new ICameraModeCommand() {
            @Override
            public void execute(MapViewCamera camera) {
                camera.setDefaultRevertDuration(durationNano);
            }
        });
    }

    private void sendCameraMessageToThread(ICameraModeCommand command) {
        if(thread != null && thread.getHandler() != null) {
            MessageDefenitions.sendExecuteMessage(thread.getHandler(), MessageDefenitions.MESSAGE_CAMERA_MODE_EXECUTE, command);
        } else {
            Log.w(TAG, "Trying to call a handler method before the thread has finished setup");
        }
    }

    @Deprecated
    public void disableFixedFPS() {
//        if(thread != null) {
//            thread.disableFixedFrameRate();
//        }
    }

    //region debugging

    public void setDebug(boolean enableDebug) {
        thread.setDebug(enableDebug);
    }

    //endregion

}
