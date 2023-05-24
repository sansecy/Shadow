package com.bytedance.boost_multidex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.util.StringTokenizer;

/**
 * Created by Xiaolin(xiaolin.gan@bytedance.com) on 2019/3/25.
 */
public class BoostMultiDex {

    public static final String BOOST_DEX = "boost_dex";

    public static Result install(Context context) {
        return install(context, context.getClassLoader(), null);
    }

    public static Result install(Context context, ClassLoader classLoader) {
        return install(context, null, classLoader, null, BOOST_DEX);
    }

    public static Result install(Context context, ClassLoader classLoader, Monitor monitor) {
        return install(context, null, classLoader, monitor, BOOST_DEX);
    }

    public static Result install(Context context, File sourceApk, ClassLoader classLoader) {
        return install(context, sourceApk, classLoader, null, BOOST_DEX);
    }

    public static Result install(Context context, File sourceApk, ClassLoader classLoader, String dirKey) {
        return install(context, sourceApk, classLoader, null, dirKey);
    }

    public static Result install(Context context, File sourceApk, ClassLoader classLoader, Monitor monitor, String dirKey) {
        Monitor.init(monitor);

        monitor = Monitor.get();

        monitor.logInfo("BoostMultiDex is installing");

        if (isVMCapable(System.getProperty("java.vm.version"))) {
            monitor.logInfo("BoostMultiDex support library is disabled for VM capable");
            return null;
        }

        if (Build.VERSION.SDK_INT < Constants.MIN_SDK_VERSION) {
            monitor.logInfo("BoostMultiDex installation failed. SDK " + Build.VERSION.SDK_INT
                    + " is unsupported. Min SDK version is " + Constants.MIN_SDK_VERSION + ".");
            return null;
        }

        Result result = Result.get();
        try {

            if (sourceApk == null) {
                ApplicationInfo applicationInfo = context.getApplicationInfo();
                if (applicationInfo == null) {
                    throw new RuntimeException("ApplicationInfo is NULL.");
                }
                sourceApk = new File(applicationInfo.sourceDir);
            }

            String processName = monitor.getProcessName();
            if (processName == null) {
                processName = Utility.getCurProcessName(context);
            }
            if (Utility.isOptimizeProcess(processName)) {
                // Force use dex bytes in opt process.
                // But a better way is avoid calling install(), then go to opt service directly.
                new DexInstallProcessor().doInstallationInOptProcess(classLoader, sourceApk);
                return null;
            } else {
                new DexInstallProcessor().doInstallation(context, sourceApk, classLoader, dirKey, result);
            }

        } catch (Throwable e) {
            monitor.logError("BoostMultiDex installation failure", e);
            result.setFatalThrowable(e);
        }
        monitor.logInfo("install done");

        return result;
    }

    public static boolean isOptimizeProcess(String processName) {
        return Utility.isOptimizeProcess(processName);
    }

    /**
     * Identifies if the current VM has a native support for multidex, meaning there is no need for
     * additional installation by this library.
     *
     * @return true if the VM handles multidex
     */
    private static boolean isVMCapable(String versionString) {
        boolean isCapable = false;
        if (versionString != null) {
            StringTokenizer tokenizer = new StringTokenizer(versionString, ".");
            String majorToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
            String minorToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
            if (majorToken != null && minorToken != null) {
                try {
                    int major = Integer.parseInt(majorToken);
                    int minor = Integer.parseInt(minorToken);
                    isCapable = (major > Constants.VM_WITH_MULTIDEX_VERSION_MAJOR)
                            || ((major == Constants.VM_WITH_MULTIDEX_VERSION_MAJOR)
                            && (minor >= Constants.VM_WITH_MULTIDEX_VERSION_MINOR));
                } catch (NumberFormatException e) {
                    // let isCapable be false
                }
            }
        }
        Monitor.get().logInfo("VM with version " + versionString +
                (isCapable ?
                        " has support" :
                        " does not have support"));
        return isCapable;
    }

}
