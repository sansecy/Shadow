package com.tencent.shadow.sample.plugin.runtime;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

public class ProcessPluginDefaultProxyActivity extends PluginContainerActivity {
    @Override
    protected String getDelegateProviderKey() {
        return "SAMPLE";
    }
}
