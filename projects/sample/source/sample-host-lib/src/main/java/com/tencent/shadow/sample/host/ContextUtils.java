package com.tencent.shadow.sample.host;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ContextUtils {
    private static final String TAG = "ContextUtils-App";
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
        String versionName = "";
        PackageInfo packageInfo = ContextUtils.getPackageInfo();
        if (packageInfo != null) {
            versionName = packageInfo.versionName;
        }
        Log.d(TAG, "init() called with: versionName = [" + versionName + "]");
    }

    public static Context getContext() {
        return sContext;
    }

    public static PackageInfo getPackageInfo() {
        PackageManager manager = sContext.getPackageManager();
        try {
            return manager.getPackageInfo(sContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
