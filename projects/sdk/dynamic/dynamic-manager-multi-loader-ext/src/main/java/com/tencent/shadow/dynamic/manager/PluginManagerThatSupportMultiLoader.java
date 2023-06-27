package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.common.ShadowLog;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.MultiLoaderPluginProcessService;
import com.tencent.shadow.dynamic.host.MultiLoaderPpsController;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.dynamic.host.PpsStatus;
import com.tencent.shadow.dynamic.loader.PluginLoader;

import java.util.HashMap;


abstract public class PluginManagerThatSupportMultiLoader extends BaseDynamicPluginManager implements PluginManagerImpl {
    private static final String TAG = "PluginManagerThatSuppor";
    private static final Logger mLogger = LoggerFactory.getLogger(PluginManagerThatSupportMultiLoader.class);

    /**
     * 插件进程MultiLoaderPluginProcessService的接口
     */
    private final HashMap<String, MultiLoaderPpsController> mPluginPpsControllerHashMap = new HashMap<>();


    public PluginManagerThatSupportMultiLoader(Context context) {
        super(context);
    }

    /**
     * 多Loader的PPS，需要hack多个RuntimeContainer，因此需要使用pluginKey来作为插件业务的身份标识
     * Note：一个插件包有一份loader、一份runtime、多个pluginPart，该key与插件包一一对应
     */


    @Override
    protected void onPluginServiceConnected(String uuid, ComponentName name, IBinder service) {
        ShadowLog.d(TAG, "onPluginServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
        mPluginPpsControllerHashMap.put(uuid, MultiLoaderPluginProcessService.wrapBinder(service));


        //todo 看看有什么需要做的
//        try {
//            IBinder iBinder = mPpsController.getPluginLoaderForPlugin(getPluginKey());
//            if (iBinder != null) {
//                mPluginLoader = new BinderPluginLoader(iBinder);
//            }
//        } catch (RemoteException ignored) {
//            if (mLogger.isErrorEnabled()) {
//                mLogger.error("onServiceConnected mPpsController getPluginLoader:", ignored);
//            }
//        }
    }

    @Override
    protected void onPluginServiceDisconnected(String uuid, ComponentName name) {
        mPluginPpsControllerHashMap.remove(uuid);
        mPluginLoaderHashMap.remove(uuid);
    }

    public final void loadRunTime(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime mPpsController: for " + uuid);
        }

        MultiLoaderPpsController ppsController = getPPSController(uuid);
        if (ppsController == null) {
            throw new IllegalStateException("manager设计错误，请先绑定服务");
        }

        try {
            ppsController.setUuidManagerForPlugin(uuid, new UuidManagerBinder(PluginManagerThatSupportMultiLoader.this));
        } catch (DeadObjectException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("onServiceConnected RemoteException:" + e);
            }
        } catch (RemoteException e) {
            if (e.getClass().getSimpleName().equals("TransactionTooLargeException")) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("onServiceConnected TransactionTooLargeException:" + e);
                }
            } else {
                throw new RuntimeException(e);
            }
        }

        PpsStatus ppsStatus = ppsController.getPpsStatusForPlugin(uuid);
        if (!ppsStatus.runtimeLoaded) {
            ppsController.loadRuntimeForPlugin(uuid, uuid);
        }
    }

    public final void loadPluginLoader(String uuid) throws RemoteException, FailedException {
        ShadowLog.d(TAG, "loadPluginLoader() called with: uuid = [" + uuid + "]");
        PluginLoader pluginLoader = getBinderPluginLoader(uuid);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader mPluginLoader:" + pluginLoader);
        }
        if (pluginLoader == null) {
            MultiLoaderPpsController ppsController = getPPSController(uuid);
            PpsStatus ppsStatus = ppsController.getPpsStatusForPlugin(uuid);
            if (!ppsStatus.loaderLoaded) {
                ppsController.loadPluginLoaderForPlugin(uuid, uuid);
            }
            IBinder iBinder = ppsController.getPluginLoaderForPlugin(uuid);
            mPluginLoaderHashMap.put(uuid, new BinderPluginLoader(iBinder));
            ShadowLog.d(TAG, "loadPluginLoader() create pluginLoader for uuid = [" + uuid + "]");
        }
    }

    /**
     * 插件加载服务端接口
     */
    private final HashMap<String, PluginLoader> mPluginLoaderHashMap = new HashMap<>();

    public PluginLoader getBinderPluginLoader(String uuid) {
        return mPluginLoaderHashMap.get(uuid);
    }

    public MultiLoaderPpsController getPPSController(String uuid) {
        return mPluginPpsControllerHashMap.get(uuid);
    }
}
