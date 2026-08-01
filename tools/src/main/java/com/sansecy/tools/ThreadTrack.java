package com.sansecy.tools;

import android.os.Looper;
import android.util.Log;

import java.util.LinkedHashMap;

public class ThreadTrack {

    private static final String TAG = "ThreadTrack-App";

    public static final LinkedHashMap<Long, Thread> threadSet = new LinkedHashMap<>();

    public static void addThread(Thread thread) {
        if (thread.getId() != Looper.getMainLooper().getThread().getId()) {
            threadSet.put(thread.getId(), thread);
            Log.d(TAG, "addThread() called with: thread size = [" + threadSet.size() + "]");
        }
    }

    public static void removeThread(Thread thread) {
        threadSet.remove(thread.getId());
    }
}
