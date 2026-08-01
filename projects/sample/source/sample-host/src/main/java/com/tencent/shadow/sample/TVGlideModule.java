package com.tencent.shadow.sample;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.NotNull;

@GlideModule
public class TVGlideModule extends AppGlideModule {
    private static final String TAG = "TVGlideModule";
    @Override
    public void applyOptions(@NotNull Context context, GlideBuilder builder) {
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565).diskCacheStrategy(DiskCacheStrategy.NONE).disallowHardwareConfig());
        long diskCacheSiz = 50 * 1024 * 1024;
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSiz));
        Log.e(TAG, "glide dis max size = " + diskCacheSiz);
        //获取系统分配给应用的总内存大小
        long maxMemory = Runtime.getRuntime().maxMemory();
        //设置图片内存缓存占用八分之一
        long memoryCacheSize = maxMemory / 8;
        //设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        Log.e(TAG, "glide memory max size = " + memoryCacheSize);
        super.applyOptions(context, builder);
    }
}
