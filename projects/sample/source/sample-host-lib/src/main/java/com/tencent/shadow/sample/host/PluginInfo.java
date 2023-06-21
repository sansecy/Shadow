package com.tencent.shadow.sample.host;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;

public class PluginInfo {

    public ApplicationInfo applicationInfo;
    public ClassLoader pluginClassLoader;
    public Resources pluginResources;
    public PackageInfo packageInfo;

    public PluginInfo(ApplicationInfo applicationInfo, ClassLoader pluginClassLoader, Resources pluginResources, PackageInfo packageInfo) {
        this.applicationInfo = applicationInfo;
        this.pluginClassLoader = pluginClassLoader;
        this.pluginResources = pluginResources;
        this.packageInfo = packageInfo;
    }
}
