/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.sample.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.common.ShadowLog;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.loader.PluginLoader;
import com.tencent.shadow.sample.constant.ShadowConstant;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class SamplePluginManager extends FastPluginManager {
    private static final String TAG = "SampleManager-Shadow";
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mCurrentContext;

    public SamplePluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "main-dynamic-manager";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName(String partKey) {
        if (partKey.equals("plugin_ar")) {
            return "com.tencent.shadow.sample.host.Plugin2ProcessPPS";
        }
//        return "com.tencent.shadow.sample.host.Plugin2ProcessPPS";
        return "com.tencent.shadow.sample.host.PluginProcessPPS";
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        if (fromId == ShadowConstant.FROM_ID_DOWNLOAD) {

        }else  if (fromId == ShadowConstant.FROM_ID_NOOP) {
            //do nothing.
        } else if (fromId == ShadowConstant.FROM_ID_INSTALL) {
            final String pluginZipPath = bundle.getString(ShadowConstant.KEY_PLUGIN_ZIP_PATH);
            if (callback != null) {
                final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
                callback.onShowLoadingView(view);
            }
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        installPlugin(pluginZipPath);
                    } catch (Exception e) {
                        ShadowLog.e(TAG, "installPlugin failed ", e);
                    }
                }
            });
        }  else if (fromId == ShadowConstant.FROM_ID_START_ACTIVITY) {
            onStartActivity(context, bundle, callback);
        } else if (fromId == ShadowConstant.FROM_ID_CLOSE) {
            close();
        } else if (fromId == ShadowConstant.FROM_ID_LOAD_VIEW_TO_HOST) {
            loadViewToHost(context, bundle);
        } else {
            throw new IllegalArgumentException("不认识的fromId==" + fromId);
        }
    }

    private void loadViewToHost(final Context context, Bundle bundle) {
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(
                context.getPackageName(),
                "cn.migu.gamehall.shadow.sample.plugin.app.lib.usecases.service.HostAddPluginViewService"
        );
        pluginIntent.putExtras(bundle);
        final String partKey = bundle.getString(ShadowConstant.KEY_PLUGIN_PART_KEY);
        if (partKey == null) {
            throw new RuntimeException("partKey 不可为空");
        }
        try {
            getBinderPluginLoader(getUUIDByPartKey(partKey)).startPluginService(pluginIntent);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void onStartActivity(final Context context, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(ShadowConstant.KEY_PLUGIN_ZIP_PATH);
        String routePath = bundle.getString(ShadowConstant.KEY_ROUTE_PATH);
        if (TextUtils.isEmpty(pluginZipPath) || !new File(pluginZipPath).exists()) {
            Log.e(TAG, "onStartActivity: pluginZipPath = " + pluginZipPath + " 插件不存在");
        }
        final Bundle extras = bundle.getBundle(ShadowConstant.KEY_EXTRAS);
        final String targetPartKey = bundle.getString(ShadowConstant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(ShadowConstant.KEY_ACTIVITY_CLASSNAME);
        final String fromPartKey = bundle.getString(ShadowConstant.KEY_PLUGIN_FROM_PART_KEY);
        //检查是否跳转到其他插件
        String uuidByPartKey = getUUIDByPartKey(targetPartKey);
        if (!TextUtils.isEmpty(fromPartKey)) {
            if (!TextUtils.isEmpty(uuidByPartKey)) {
                PluginLoader binderPluginLoader = getBinderPluginLoader(uuidByPartKey);
                if (binderPluginLoader != null) {
                    if (!TextUtils.isEmpty(targetPartKey)) {
                        if (!TextUtils.isEmpty(routePath)) {
                            try {
                                Intent intent = binderPluginLoader.convertActivityIntent(getPluginIntent(context, className, extras, bundle));
                                binderPluginLoader.startActivityInPluginProcess(intent);
                                return;
                            } catch (RemoteException e) {
                                ShadowLog.e(TAG, "startActivityInPluginProcess Oops ", e);
                            }
                        }
                    }
                }
            }
        }



        if (targetPartKey == null) {
            throw new RuntimeException("partKey 不可为空");
        }


        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            callback.onShowLoadingView(view);
        }

        executorService.execute(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    String localPartKey = targetPartKey;
                    installPlugin(pluginZipPath);
                    String uuid = getUUIDByPartKey(targetPartKey);
                    if (!TextUtils.isEmpty(className)) {
                        Intent pluginIntent = getPluginIntent(context, className, extras, bundle);

                        long localStartTime = System.currentTimeMillis();
                        Intent intent = getBinderPluginLoader(uuid).convertActivityIntent(pluginIntent);
//                        long tookTime = System.currentTimeMillis() - startTime;
//                        Log.d(TAG, String.format("convertActivityIntent() took %d ms", tookTime));

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getBinderPluginLoader(uuid).startActivityInPluginProcess(intent);
                        Log.d(TAG, String.format("startActivityInPluginProcess() took %d ms", (System.currentTimeMillis() - localStartTime)));
                    } else if (!TextUtils.isEmpty(routePath)) {
                        Intent pluginIntent = new Intent();
                        pluginIntent.replaceExtras(bundle);
                        pluginIntent.putExtra(ShadowConstant.KEY_ROUTE_PATH, routePath);
                        long localStartTime = System.currentTimeMillis();
                        Intent intent = getBinderPluginLoader(uuid).convertActivityIntent(pluginIntent);
//                        long tookTime = System.currentTimeMillis() - startTime;
//                        Log.d(TAG, String.format("convertActivityIntent() took %d ms", tookTime));

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getBinderPluginLoader(uuid).startActivityInPluginProcess(intent);
                        Log.d(TAG, String.format("startActivityInPluginProcess() took %d ms", (System.currentTimeMillis() - localStartTime)));
                    } else {
                        Log.e(TAG, "className and routePath is null,not jump");
                    }
                    long tookTime = System.currentTimeMillis() - startTime;
                    Log.d(TAG, String.format("onStartActivity() install %s took %d ms", localPartKey, tookTime));
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                    throw new RuntimeException(e.getMessage());
                }
                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });
    }

    private Intent getPluginIntent(Context context, String className, Bundle extras, Bundle bundle) {
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(
                context.getPackageName(),
                className
        );
        if (extras != null) {
            pluginIntent.replaceExtras(extras);
        }
        pluginIntent.replaceExtras(bundle);
        return pluginIntent;
    }

    private void installPlugin(String pluginZipPath) throws IOException, JSONException, InterruptedException, ExecutionException, RemoteException, TimeoutException, FailedException {
        if (!TextUtils.isEmpty(pluginZipPath)) {
            String name = new File(pluginZipPath).getName();
            InstalledPlugin installedPlugin = installPlugin(pluginZipPath, name, false);
//                    Set<String> strings = installedPlugin.plugins.keySet();
            initAllPlugin(installedPlugin);
        } else {
            List<InstalledPlugin> installedPlugins = getInstalledPlugins(1);
            if (installedPlugins.size() > 0) {
                InstalledPlugin installedPlugin = installedPlugins.get(0);
                initAllPlugin(installedPlugin);
            }
        }
    }

    private void initAllPlugin(InstalledPlugin installedPlugin) throws RemoteException, TimeoutException, FailedException {
        Map<String, InstalledPlugin.PluginPart> plugins = installedPlugin.plugins;
        for (String s : plugins.keySet()) {
            InstalledPlugin.PluginPart pluginPart = plugins.get(s);
            if (pluginPart != null) {
                String[] dependsOn = pluginPart.dependsOn;
                if (dependsOn != null && dependsOn.length > 0) {
                    String dependOnKey = dependsOn[0];
                    InstalledPlugin.PluginPart dependOnPlugin = plugins.get(dependOnKey);
                    if (dependOnPlugin != null) {
                        init(installedPlugin, dependOnKey);
                    }
                }
                init(installedPlugin, s);
            }
        }
    }

    private final HashMap<String, String> mPartKeyToUUIDHashMap = new HashMap<>();

    public String getUUIDByPartKey(String partKey) {
        String UUID = mPartKeyToUUIDHashMap.get(partKey);
        ShadowLog.d(TAG, "getUUIDByPartKey() called with: partKey = [" + partKey + "] UUID = " + UUID);
        return UUID;
    }

    private void init(InstalledPlugin installedPlugin, String partKey) throws RemoteException, TimeoutException, FailedException {
        ShadowLog.d(TAG, "init() called with: installedPlugin.UUID = [" + installedPlugin.UUID + "], partKey = [" + partKey + "]");
        mPartKeyToUUIDHashMap.put(partKey, installedPlugin.UUID);
        loadPlugin(installedPlugin.UUID, partKey);
        callApplicationOnCreate(installedPlugin.UUID, partKey);
    }
}
