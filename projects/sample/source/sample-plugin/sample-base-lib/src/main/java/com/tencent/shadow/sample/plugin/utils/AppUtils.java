package com.tencent.shadow.sample.plugin.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

public class AppUtils {
    private static final String TAG = "AppUtils";
    public static void setWindow(Activity activity) {
        activity.getWindow().setAttributes(new WindowManager.LayoutParams(-1, -1));
    }
    public static void setContext(Context context) {
        String packageCodePath = context.getPackageCodePath();
        Log.d(TAG, "setContext() called with: packageCodePath = [" + packageCodePath + "]");
    }
}
