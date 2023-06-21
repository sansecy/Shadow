package cn.migu.gamehall.shadow.core.transform.specific

class AppComponentFactoryTransform(transformSkipClass: () -> Array<String>) : SimpleRenameTransform(transformSkipClass,
    mapOf(
        "android.app.AppComponentFactory"
                to "cn.migu.gamehall.shadow.core.runtime.ShadowAppComponentFactory"
    )
)
