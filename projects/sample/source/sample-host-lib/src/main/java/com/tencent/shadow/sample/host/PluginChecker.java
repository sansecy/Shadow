package com.tencent.shadow.sample.host;

import android.os.Bundle;
import android.util.Log;

import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.sample.constant.ShadowConstant;
import com.tencent.shadow.sample.host.manager.Shadow;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class PluginChecker {
    private static final String TAG = "PluginChecker";
    private PluginManager mPluginManager;

    public PluginManager getPluginManager() {
        return mPluginManager;
    }

    public void loadPluginManager(File apk) {
        if (mPluginManager == null) {
            mPluginManager = Shadow.getPluginManager(apk);
        }
    }

    private static PluginChecker sPluginChecker = new PluginChecker();

    private HashMap<String, PluginInfo> mStringApplicationInfoHashMap = new HashMap<>();

    public static PluginChecker getInstance() {
        return sPluginChecker;
    }

    public PluginInfo get(String partKey) {
        return mStringApplicationInfoHashMap.get(partKey);
    }

    public void put(String partKey, PluginInfo pluginInfo) {
        mStringApplicationInfoHashMap.put(partKey, pluginInfo);
    }

    public Collection<PluginInfo> getAllPluginInfo() {
        return mStringApplicationInfoHashMap.values();
    }
    public Set<String> getAllPluginKey() {
        return mStringApplicationInfoHashMap.keySet();
    }

    public Set<String> getKeys() {
        return mStringApplicationInfoHashMap.keySet();
    }

    public void loadPlugin(String partKey, File file) {
        Log.d(TAG, "loadPlugin() called with: partKey = [" + partKey + "], file = [" + file + "]");
        try {
            Bundle bundle = new Bundle();
            bundle.putString(ShadowConstant.KEY_PLUGIN_PART_KEY, partKey);
            bundle.putString(ShadowConstant.KEY_PLUGIN_ZIP_PATH, file.getAbsolutePath());
            getInstance().getPluginManager()
                    .enter(ContextUtils.getContext(), ShadowConstant.FROM_ID_START_ACTIVITY, bundle, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "loadPlugin() ERROR called with: partKey = [" + partKey + "], file = [" + file + "]");
        }
    }
}
