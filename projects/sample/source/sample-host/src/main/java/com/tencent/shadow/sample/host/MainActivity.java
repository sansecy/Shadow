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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.shadow.sample.constant.ShadowConstant;
import com.tencent.shadow.sample.host.plugin_view.HostAddPluginViewActivity;

import java.io.File;


public class MainActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TestHostTheme);
        mContext = this;

        LinearLayout rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);

        TextView infoTextView = new TextView(this);
        infoTextView.setText(R.string.main_activity_info);
        rootView.addView(infoTextView);

        final Spinner partKeySpinner = new Spinner(this);
        ArrayAdapter<String> partKeysAdapter = new ArrayAdapter<>(this, R.layout.part_key_adapter);
        partKeysAdapter.addAll(
                ShadowConstant.PART_KEY_PLUGIN_MAIN_APP,
                ShadowConstant.PART_KEY_PLUGIN_ANOTHER_APP
        );
        partKeySpinner.setAdapter(partKeysAdapter);

        rootView.addView(partKeySpinner);

        Button startPluginButton = new Button(this);
        startPluginButton.setText(R.string.start_plugin);
        startPluginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String partKey = (String) partKeySpinner.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, PluginLoadActivity.class);
                intent.putExtra(ShadowConstant.KEY_PLUGIN_PART_KEY, partKey);
                intent.putExtra(ShadowConstant.KEY_ACTIVITY_CLASSNAME, "com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityOnCreate");
                startActivity(intent);
            }
        });
        rootView.addView(startPluginButton);
//        Button pluginStart = new Button(this);
//        pluginStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Bundle bundle = new Bundle();
////                bundle.putString(ShadowConstant.KEY_PLUGIN_ZIP_PATH, new File(mContext.getFilesDir(), PluginHelper.sPluginZip).getPath());
////                bundle.putString(ShadowConstant.KEY_ACTIVITY_CLASSNAME, "com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityOnCreate");
////                HostApplication.getApp().getPluginManager().enter(mContext, ShadowConstant.FROM_ID_START_ACTIVITY, bundle, null);
//
//            }
//        });
//        rootView.addView(pluginStart);
//        Button uninstallBtn = new Button(this);
//        uninstallBtn.setText("卸载gamehall");
//        uninstallBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString(ShadowConstant.KEY_UNINSTALL_UUID, "gamehall");
//                HostApplication.getApp().getPluginManager().enter(mContext, ShadowConstant.FROM_ID_START_ACTIVITY, bundle, null);
//            }
//        });
//        rootView.addView(uninstallBtn);

        Button AR = new Button(this);
        AR.setText("AR");
        AR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ShadowConstant.KEY_PLUGIN_ZIP_PATH, PluginHelper.getInstance().pluginLauncherZipFile.getPath());
                bundle.putString(ShadowConstant.KEY_ACTIVITY_CLASSNAME, "cn.migudm.ar.module.home.mvvm.ui.MainActivity");
                HostApplication.getApp().getPluginManager().enter(mContext, ShadowConstant.FROM_ID_START_ACTIVITY, bundle, null);
            }
        });
        rootView.addView(AR);

//        Button pluginStart2 = new Button(this);
//        rootView.addView(pluginStart2);
//        pluginStart2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString(ShadowConstant.KEY_PLUGIN_ZIP_PATH, new File(mContext.getFilesDir(), PluginHelper.sPluginZip2).getPath());
//                bundle.putString(ShadowConstant.KEY_ACTIVITY_CLASSNAME, "com.tencent.shadow.sample.plugin.app.lib.MainActivity");
//                HostApplication.getApp().getPluginManager().enter(mContext, ShadowConstant.FROM_ID_START_ACTIVITY, bundle, null);
//            }
//        });
//        Button startHostAddPluginViewActivityButton = new Button(this);
//        startHostAddPluginViewActivityButton.setText("宿主添加插件View");
//        startHostAddPluginViewActivityButton.setOnClickListener(v -> {
//            Intent intent = new Intent(this, HostAddPluginViewActivity.class);
//            startActivity(intent);
//        });
//        rootView.addView(startHostAddPluginViewActivityButton);

        setContentView(rootView);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
