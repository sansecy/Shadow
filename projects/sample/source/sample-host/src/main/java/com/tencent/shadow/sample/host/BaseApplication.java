package com.tencent.shadow.sample.host;

import android.app.Application;
import android.util.Log;

public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        String currentProcessName = ProcessUtil.getCurrentProcessName(this);
        Log.d(TAG, "onCreate() called currentProcessName = " + currentProcessName);
    }
}
