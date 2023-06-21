package cn.migu.gamehall.shadow.dynamic.manager;

import cn.migu.gamehall.shadow.core.common.InstalledApk;
import cn.migu.gamehall.shadow.dynamic.host.FailedException;
import cn.migu.gamehall.shadow.dynamic.host.NotFoundException;

public interface UuidManagerImpl {
    InstalledApk getPlugin(String uuid, String partKey) throws NotFoundException, FailedException;

    InstalledApk getPluginLoader(String uuid) throws NotFoundException, FailedException;

    InstalledApk getRuntime(String uuid) throws NotFoundException, FailedException;
}
