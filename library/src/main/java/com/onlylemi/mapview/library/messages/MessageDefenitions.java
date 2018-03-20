package com.onlylemi.mapview.library.messages;

import android.os.Handler;
import android.os.Message;

/**
 * Created by patnym on 03/12/2017.
 */

public class MessageDefenitions {

    public static final int MESSAGE_EXIT_THREAD = 255;
    public static final int MESSAGE_DRAW = 256;
    public static final int MESSAGE_EXECUTE = 257;
    public static final int MESSAGE_CAMERA_MODE_EXECUTE = 258;
    public static final int MESSAGE_MOTIONEVENT = 259;
    public static final int MESSAGE_SURFACE_CHANGED = 260;

    public static void sendExecuteMessage(Handler handler, int what, ICommand commandToExecute) {
        handler.sendMessage(Message.obtain(handler, what, commandToExecute));
    }

    public static void sendExecuteMessage(Handler handler, int what, ICameraModeCommand commandToExecute) {
        handler.sendMessage(Message.obtain(handler, what, commandToExecute));
    }
}
