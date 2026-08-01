package com.tencent.shadow.dynamic.host;

import android.os.IBinder;

public interface BinderService {
    void loadRuntimeForPlugin(String pluginKey, String uuid) throws FailedException;

    void loadPluginLoaderForPlugin(String pluginKey, String uuid) throws FailedException;

    void setUuidManagerForPlugin(String pluginKey, UuidManager uuidManager);

    PpsStatus getPpsStatusForPlugin(String pluginKey);

    IBinder getPluginLoaderForPlugin(String pluginKey);

    void exit();
}
