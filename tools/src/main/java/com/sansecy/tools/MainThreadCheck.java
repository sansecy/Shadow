package com.sansecy.tools;

import android.os.Looper;

public class MainThreadCheck {
    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
