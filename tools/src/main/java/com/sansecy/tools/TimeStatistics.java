package com.sansecy.tools;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeStatistics {
    public static long appLaunchTime;
    public static double appLaunchCostTime;
    private static final String TAG = "TimeStatistics-App";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

//    private static final FilePrinter sPrinter;
    public static int pid;

    static {
//        FilePrinter.Builder builder = new FilePrinter.Builder(Environment.getExternalStorageDirectory() + "/sp_time");
//        sPrinter = builder.build();
    }

    public static void startLaunch() {
        appLaunchTime = LogTime.getLogTime();
        Log.d(TAG, "appLaunch in " + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "");
//        String msg = "appLaunch in " + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "";
//        sPrinter.println(LogLevel.DEBUG, "TimeStatistics", msg);
    }

    public static void endLaunch() {
        appLaunchCostTime = LogTime.getElapsedMillis(appLaunchTime);
//        String msg = "appLaunch cost " + appLaunchCostTime + "ms";
//        sPrinter.println(LogLevel.DEBUG, "TimeStatistics", msg);
        Log.d(TAG, "appLaunch cost " + appLaunchCostTime + "ms");

    }

    public static void endLaunch(String position) {
        appLaunchCostTime = LogTime.getElapsedMillis(appLaunchTime);
//        String msg = "appLaunch " + position + " cost " + appLaunchCostTime + "ms";
//        sPrinter.println(LogLevel.DEBUG, "TimeStatistics", msg);
        Log.d(TAG, "appLaunch " + position + " cost " + appLaunchCostTime + "ms");

    }

    public static long getAppLaunchTime() {
        return appLaunchTime;
    }

    public static long getTime() {
        return LogTime.getLogTime();
    }

    public static void logTime(String message, long time) {
        double elapsedMillis = LogTime.getElapsedMillis(time);
//        String msg = "logTime  " + message + " " + elapsedMillis + "ms";
//        sPrinter.println(LogLevel.DEBUG, "TimeStatistics", msg);
        Log.d(TAG, "logTime  " + message + " " + elapsedMillis + "");

    }

    public static void log(String message) {
//        String msg = "appLaunch " + message;
//        sPrinter.println(LogLevel.DEBUG, "TimeStatistics", msg);
        Log.d(TAG, "appLaunch " + message);
    }
}
