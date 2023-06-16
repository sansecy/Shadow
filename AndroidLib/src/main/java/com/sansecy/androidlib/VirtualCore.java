package com.sansecy.androidlib;

import mirror.android.app.ActivityThread;

public class VirtualCore {
    private Object mainThread;
    private static VirtualCore virtualCore = new VirtualCore();

    public static VirtualCore get() {
        return virtualCore;
    }

    public VirtualCore() {
        mainThread = ActivityThread.currentActivityThread.call();
    }

    public static Object mainThread() {
        return get().mainThread;
    }
}
