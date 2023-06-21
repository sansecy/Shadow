package com.sansecy.tools;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Set;

public class MethodTracker {
    private static final String TAG = "MethodTracker-App";
    private static MethodTracker sInstance = new MethodTracker();
    private final LinkedHashMap<String, Long> map = new LinkedHashMap<>();

    public static String stopName = "";
    public static boolean stopped;

    public static MethodTracker getInstance() {
        return sInstance;
    }

    public static void collect(String name, long time) {
        Log.d(TAG, "collect() called with: name = [" + name + "], time = [" + time + "]");
        if (stopped) {
            return;
        }
        if (stopName.equals(name)) {
            sInstance.map.put(name, time);
            stopped = true;
            FileOutputStream fileOutputStream = null;
            try {
                 fileOutputStream = new FileOutputStream(ContextUtils.getContext().getCacheDir() + "/MethodTracker.txt");
                Set<String> strings = sInstance.map.keySet();
                for (String string : strings) {
                    Long aLong = sInstance.map.get(string);
                    fileOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write("   ".getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write(String.valueOf(aLong).getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                }
                fileOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sInstance.map.clear();

            return;
        }
        sInstance.map.put(name, time);
    }
}
