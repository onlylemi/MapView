package com.onlylemi.mapview.library.messages;

import android.os.Handler;
import android.os.Message;

/**
 * Created by patnym on 03/12/2017.
 */

public class MessageDefenitions {

    public static int MESSAGE_DRAW = 1;
    public static int MESSAGE_EXECUTE = 2;
    public static int MESSAGE_CAMERA_MODE_EXECUTE = 3;

    public static void sendExecuteMessage(Handler handler, int what, ICommand commandToExecute) {
        handler.sendMessage(Message.obtain(handler, what, commandToExecute));
    }

    public static void sendExecuteMessage(Handler handler, int what, ICameraModeCommand commandToExecute) {
        handler.sendMessage(Message.obtain(handler, what, commandToExecute));
    }
}
