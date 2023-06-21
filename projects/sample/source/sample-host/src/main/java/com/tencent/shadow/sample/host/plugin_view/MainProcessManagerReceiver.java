package com.tencent.shadow.sample.host.plugin_view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.shadow.sample.constant.ShadowConstant;
import com.tencent.shadow.sample.host.HostApplication;

public class MainProcessManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        HostApplication.getApp().getPluginManager()
                .enter(context, ShadowConstant.FROM_ID_LOAD_VIEW_TO_HOST, intent.getExtras(), null);
    }
}
