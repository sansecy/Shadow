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

package com.tencent.shadow.sample.host;

import android.content.Context;
import android.util.Log;

import com.tencent.shadow.sample.host.lib.BuildConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginHelper {
    private static final String TAG = "PluginHelper-shadow";

    /**
     * 动态加载的插件管理apk
     */
    public final static String sPluginManagerName = "pluginmanager.apk";

    /**
     * 动态加载的插件包，里面包含以下几个部分，插件apk，插件框架apk（loader apk和runtime apk）, apk信息配置关系json文件
     */
    public final static String sMainPluginZip = "plugin-main.zip";
    public final static String sCloudPluginZip = "plugin-cloud.zip";
    public final static String spluginLauncherZipName = "plugin-launcher.zip";
    public File pluginManagerFile;

    public File pluginLauncherZipFile;
    public ExecutorService singlePool = Executors.newSingleThreadExecutor();

    private Context mContext;

    private static PluginHelper sInstance = new PluginHelper();

    public static PluginHelper getInstance() {
        return sInstance;
    }

    private PluginHelper() {
    }

    public void init(Context context) {
        pluginManagerFile = new File(context.getFilesDir(), sPluginManagerName);
        File shadowPluginDir = new File(context.getFilesDir(), "shadow_plugin");
        shadowPluginDir.mkdirs();
        pluginLauncherZipFile = new File(shadowPluginDir, spluginLauncherZipName);
//        pluginZipFile = new File(Environment.getExternalStorageDirectory(), sPluginZip);
        mContext = context.getApplicationContext();
        singlePool.execute(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                preparePlugin();
                Log.d(TAG, "init() called preparePlugin took " + (System.currentTimeMillis() - start) + " ms");
                long start1 = System.currentTimeMillis();
                PluginChecker.getInstance().loadPluginManager(PluginHelper.getInstance().pluginManagerFile);
                Log.d(TAG, "init() called load manager took  " + (System.currentTimeMillis() - start1) + " ms");
            }
        });

    }

    public void preparePlugin() {
        try {
            Log.d(TAG, "preparePlugin() begin ");
            if (!pluginManagerFile.exists()) {
                InputStream is = mContext.getAssets().open(sPluginManagerName);
                FileUtils.copyInputStreamToFile(is, pluginManagerFile);
                Log.d(TAG, "preparePlugin() called copy sPluginManagerName to " + pluginManagerFile);
            }
            if (!pluginLauncherZipFile.exists()) {
                InputStream zip = mContext.getAssets().open(spluginLauncherZipName);
                FileUtils.copyInputStreamToFile(zip, pluginLauncherZipFile);
                Log.d(TAG, "preparePlugin() called copy " + spluginLauncherZipName + " from assets to " + pluginLauncherZipFile);
            }
            Log.d(TAG, "preparePlugin() done ");
        } catch (IOException e) {
//            throw new RuntimeException("从assets中复制apk出错", e);
        }
    }

}
