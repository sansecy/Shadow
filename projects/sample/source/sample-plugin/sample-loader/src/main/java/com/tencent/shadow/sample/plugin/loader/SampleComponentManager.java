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

package com.tencent.shadow.sample.plugin.loader;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.ShadowLog;
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import com.tencent.shadow.core.runtime.ShadowContext;
import com.tencent.shadow.core.runtime.container.GeneratedHostActivityDelegator;
import com.tencent.shadow.sample.constant.ShadowConstant;
import com.tencent.shadow.sample.host.PluginChecker;
import com.tencent.shadow.sample.host.PluginInfo;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SampleComponentManager extends ComponentManager {
    private static final String TAG = "SampleComponentManager-Shadow";
    /**
     * dynamic-runtime-apk 模块中定义的壳子Activity，需要在宿主AndroidManifest.xml注册
     */
    private static final String DEFAULT_ACTIVITY = "com.tencent.shadow.sample.plugin.runtime.PluginDefaultProxyActivity";
    private static final String SINGLE_INSTANCE_ACTIVITY = "com.tencent.shadow.sample.plugin.runtime.PluginSingleInstance1ProxyActivity";
    private static final String SINGLE_TASK_ACTIVITY = "com.tencent.shadow.sample.plugin.runtime.PluginSingleTask1ProxyActivity";
    private static final String ProcessPluginDefaultProxyActivity = "com.tencent.shadow.sample.plugin.runtime.ProcessPluginDefaultProxyActivity";
    private static final String ProcessPluginSingleInstance1ProxyActivity = "com.tencent.shadow.sample.plugin.runtime.ProcessPluginSingleInstance1ProxyActivity";
    private static final String ProcessPluginSingleTask1ProxyActivity = "com.tencent.shadow.sample.plugin.runtime.ProcessPluginSingleTask1ProxyActivity";
    public static final String PLUGIN_AR = "plugin_ar";

    private Context context;

    public SampleComponentManager(Context context) {
        this.context = context;
    }


    /**
     * 配置插件Activity 到 壳子Activity的对应关系
     *
     * @param pluginActivity 插件Activity
     * @return 壳子Activity
     */
    @Override
    public ComponentName onBindContainerActivity(String partKey, ComponentName pluginActivity) {
        ComponentName componentName;
        String className = pluginActivity.getClassName();
        switch (className) {
            /**
             * 这里配置对应的对应关系
             */
//            case "cn.migudm.ar.module.home.mvvm.ui.MainActivity":
//                componentName = new ComponentName(context, ProcessPluginDefaultProxyActivity);
//                break;
//            default:
//                componentName = new ComponentName(context, DEFAULT_ACTIVITY);
        }
        if (partKey.equals(PLUGIN_AR)) {
            componentName = new ComponentName(context, ProcessPluginDefaultProxyActivity);
        } else {
            //todo loader最好按照插件分，否则需要把插件所有的Activity作配对关系
            componentName = new ComponentName(context, DEFAULT_ACTIVITY);
        }
        ShadowLog.d(TAG, "onBindContainerActivity() called with: className = [" + className + "] to [" + componentName.getClassName() + "]");
        return componentName;
    }

    /**
     * 配置对应宿主中预注册的壳子contentProvider的信息
     */
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(String partKey, ComponentName pluginContentProvider) {
        String className;
        String authority;
        if (partKey.equals(PLUGIN_AR)) {
            className = "com.tencent.shadow.core.runtime.container.PluginProcessContainerContentProvider";
            authority = context.getPackageName() + ".contentprovider.authority.dynamic.process";
        } else {
            className = "com.tencent.shadow.core.runtime.container.PluginContainerContentProvider";
            authority = context.getPackageName() + ".contentprovider.authority.dynamic";
        }
        ShadowLog.d(TAG, "onBindContainerContentProvider() called with: pluginContentProvider = [" + pluginContentProvider.getClassName() + "], to provider = [" + className + "]");
        return new ContainerProviderInfo(
                className,
                authority);
    }

    @Override
    public boolean startActivity(ShadowContext shadowContext, Intent pluginIntent, Bundle option) {
        return super.startActivity(shadowContext, pluginIntent, option);
    }

    @Override
    public boolean startActivityForResult(GeneratedHostActivityDelegator delegator, Intent pluginIntent, int requestCode, Bundle option, ComponentName callingActivity) {
        boolean activity = super.startActivityForResult(delegator, pluginIntent, requestCode, option, callingActivity);
//        String aPackage = pluginIntent.getPackage();
//        if (aPackage.equals(delegator.getPackageName())) {
//            //跳转到自身，是那么可能是宿主或者插件
//
//        }
        return activity;
    }

    @SuppressLint("LongLogTag")
    @Override
    public Intent convertPluginActivityIntent(Intent pluginIntent) {
        ClassLoader classLoader = Logger.class.getClassLoader();
        pluginIntent.setExtrasClassLoader(classLoader);
        String routePath = pluginIntent.getStringExtra(ShadowConstant.KEY_ROUTE_PATH);
        ShadowLog.d(TAG, "convertPluginActivityIntent: 加载路由 " + routePath);
        if (routePath != null) {
//            Set<String> keys = PluginChecker.getInstance().getKeys();
            Set<String> routeClasses = new HashSet<>();
            routeClasses.add("com.migugame.router" + ".RouterApp$" + "article_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "article_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "cloud_service_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "cloudgame_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "competition_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "freeplay_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "game_detail_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "home_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "horizontal_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "light_hand_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "list_datas_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "live_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "mark_push_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "smallgame_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "teen_module");
//            routeClasses.add("com.migugame.router" + ".RouterApp$" + "userinfo_module");
            for (String routeClassName : routeClasses) {
                Set<String> allPluginKey = PluginChecker.getInstance().getAllPluginKey();
                for (String pluginKey : allPluginKey) {
                    PluginInfo pluginInfo = PluginChecker.getInstance().get(pluginKey);
                    ShadowLog.e(TAG, "convertPluginActivityIntent() called with: route_name = [" + routeClassName + "]");
                    try {
                        Class<?> RouterApp = pluginInfo.pluginClassLoader.loadClass(routeClassName);
                        Object instance = RouterApp.newInstance();
                        Method init = RouterApp.getDeclaredMethod("init");
                        init.invoke(instance);
                        Method getClass = RouterApp.getSuperclass().getDeclaredMethod("getClass", String.class);
                        Class<?> clazz = (Class<?>) getClass.invoke(instance, routePath);
                        if (clazz == null) {
                            continue;
                        }
                        String name = clazz.getName();
                        ComponentName component = new ComponentName(context, name);
                        pluginIntent.setComponent(component);
                        Intent pluginActivityIntent = super.convertPluginActivityIntent(pluginIntent);
                        ShadowLog.e(TAG, "convertPluginActivityIntent: 找到路由 [" + routePath + "] 对应Activity [" + name + "] in plugin for [" + pluginKey + "]");
                        return pluginActivityIntent;
                    } catch (ClassNotFoundException e) {
                        ShadowLog.e(TAG, "convertPluginActivityIntent: 没有找到routeClassName [" + routeClassName + "] " + " in plugin for [" + pluginKey + "]");
                    } catch (Exception e) {
                        ShadowLog.e(TAG, "convertPluginActivityIntent: 没有找到路由 [" + routePath + "] 对应Activity" + " in plugin for [" + pluginKey + "]");
                    }
                }
            }
        }
        return super.convertPluginActivityIntent(pluginIntent);
    }
}
