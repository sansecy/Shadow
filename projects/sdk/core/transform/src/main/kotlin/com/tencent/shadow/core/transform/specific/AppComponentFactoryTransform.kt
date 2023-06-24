package com.tencent.shadow.core.transform.specific

class AppComponentFactoryTransform(transformSkipClass: () -> Array<String>) : SimpleRenameTransform(transformSkipClass,
    mapOf(
        "android.app.AppComponentFactory"
                to "com.tencent.shadow.core.runtime.ShadowAppComponentFactory"
    )
)
