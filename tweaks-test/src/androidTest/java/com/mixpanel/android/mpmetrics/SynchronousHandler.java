package com.mixpanel.android.mpmetrics;

import android.os.Handler;
import android.os.Message;

/**
 * Stub/Mock handler that just runs stuff synchronously
 */
public class SynchronousHandler extends Handler {
    @Override
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        dispatchMessage(msg);
        return true;
    }
}
