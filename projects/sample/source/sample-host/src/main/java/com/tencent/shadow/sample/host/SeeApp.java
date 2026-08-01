package com.tencent.shadow.sample.host;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SeeApp {
    public static boolean existed;

    static {
        try {
            Class.forName("com.sansecy.monitor.see.See");
            existed = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            existed = false;
        }
    }

    public static boolean isEnabled() {
        return existed;
    }

    private static Method addPlugin;
    private static Method showLog;
    private static Method showError;
    private static Method showException;
    private static Method showFail;
    private static Method showSuccess;

    public static void init(Application application) {
        if (existed) {
            try {
                Class<?> See = Class.forName("com.sansecy.monitor.see.See");
                See.getDeclaredMethod("init", Application.class).invoke(null, application);
                addPlugin = See.getDeclaredMethod("addPlugin", CharSequence.class, Runnable.class);
                showLog = See.getDeclaredMethod("showLog", Integer.class, String.class, String.class);
                showError = See.getDeclaredMethod("showError", Integer.class, String.class, String.class);
                showException = See.getDeclaredMethod("showError", Integer.class, String.class, Exception.class);
                showFail = See.getDeclaredMethod("showFail", Integer.class, String.class, String.class);
                showSuccess = See.getDeclaredMethod("showSuccess", Integer.class, String.class, String.class);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addPlugin(String action, Runnable runnable) {
        if (existed) {
            if (addPlugin != null) {
                try {
                    addPlugin.invoke(null, action, runnable);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showLog(@NonNull Integer logId, @Nullable String tag, @Nullable String message) {
        if (showLog != null) {
            try {
                showLog.invoke(null, logId, tag, message);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showError(@Nullable Integer logId, @Nullable String tag, @NotNull String message) {
        if (showError != null) {
            try {
                showError.invoke(null, logId, tag, message);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showFail(@Nullable Integer logId, @Nullable String tag, @Nullable String message) {
        if (showFail != null) {
            try {
                showFail.invoke(null, logId, tag, message);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showSuccess(@Nullable Integer logId, @Nullable String tag, @Nullable String message) {
        if (showSuccess != null) {
            try {
                showSuccess.invoke(null, logId, tag, message);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showError(@Nullable Integer logId, @Nullable String tag, @NotNull Exception e) {
        if (showException != null) {
            try {
                showException.invoke(null, logId, tag, e);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
        }
    }
}
