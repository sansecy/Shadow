package com.sansecy.tools;

import android.content.Context;

public class ContextUtils {
    private static final String TAG = "ContextUtils-App";
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
