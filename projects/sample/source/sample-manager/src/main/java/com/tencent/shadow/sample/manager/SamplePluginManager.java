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

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.sample.constant.Constant;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class SamplePluginManager extends FastPluginManager {
    private static final String TAG = "SamplePluginManager-Shadow";
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
        return "test-dynamic-manager";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName(String partKey) {
        //如果有默认PPS，可用return代替throw
        return "com.tencent.shadow.sample.host.PluginProcessPPS";
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        if (fromId == Constant.FROM_ID_NOOP) {
            //do nothing.
        } else if (fromId == Constant.FROM_ID_START_ACTIVITY) {
            onStartActivity(context, bundle, callback);
        } else if (fromId == Constant.FROM_ID_CLOSE) {
            close();
        } else if (fromId == Constant.FROM_ID_LOAD_VIEW_TO_HOST) {
            loadViewToHost(context, bundle);
        } else {
            throw new IllegalArgumentException("不认识的fromId==" + fromId);
        }
    }

    private void loadViewToHost(final Context context, Bundle bundle) {
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(
                context.getPackageName(),
                "com.tencent.shadow.sample.plugin.app.lib.usecases.service.HostAddPluginViewService"
        );
        pluginIntent.putExtras(bundle);
        try {
            mPluginLoader.startPluginService(pluginIntent);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("LongLogTag")
    private void onStartActivity(final Context context, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        if (TextUtils.isEmpty(pluginZipPath) || !new File(pluginZipPath).exists()) {
            Log.e(TAG, "onStartActivity: " + pluginZipPath + " 插件不存在");
            return;
        }
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        if (partKey == null) {
            throw new RuntimeException("partKey 不可为空");
        }
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);

        final Bundle extras = bundle.getBundle(Constant.KEY_EXTRAS);

        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            callback.onShowLoadingView(view);
        }

        long startTime = System.currentTimeMillis();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String localPartKey = partKey;
                    InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, false);
//                    Set<String> strings = installedPlugin.plugins.keySet();
                    init(installedPlugin, partKey);
//                    init(installedPlugin, "sample-base");
//                    init(installedPlugin, "mainpage");
//                    init(installedPlugin, "cloudgame");

                    if (!TextUtils.isEmpty(className)) {
                        Intent pluginIntent = new Intent();
                        pluginIntent.setClassName(
                                context.getPackageName(),
                                className
                        );
                        if (extras != null) {
                            pluginIntent.replaceExtras(extras);
                        }
                        Intent intent = mPluginLoader.convertActivityIntent(pluginIntent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mPluginLoader.startActivityInPluginProcess(intent);


                    } else {
                        Log.e(TAG, "className is null");
                    }
                    long tookTime = System.currentTimeMillis() - startTime;
                    Log.d(TAG, String.format("onStartActivity() install %s took %d ms", localPartKey, tookTime));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });
    }

    private void init(InstalledPlugin installedPlugin, String partKey) throws RemoteException, TimeoutException, FailedException {
        Log.d(TAG, "init() called with: installedPlugin = [" + installedPlugin + "], partKey = [" + partKey + "]");
        loadPlugin(installedPlugin.UUID, partKey);
        callApplicationOnCreate(partKey);
    }
}
