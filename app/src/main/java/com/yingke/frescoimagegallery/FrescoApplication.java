package com.yingke.frescoimagegallery;

import android.app.Application;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;

import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class FrescoApplication extends Application {

    public static final String TMP_IMG_DIR = "TMP_IMG_DIR";
    public static int MAX_MEM = 15 * ByteConstants.MB;
    public static int MAX_CACHE_SIZE = Integer.MAX_VALUE;

    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
    }

    /**
     * 初始化fresco
     */
    private void initFresco() {
        String path = null;
        File cacheDir = null;
        try {
            cacheDir = getApplicationContext().getCacheDir();
        } catch (Exception e) {
        }
        if (cacheDir != null) {
            path = cacheDir.getAbsolutePath();
        }
        final MemoryCacheParams memoryCacheParams = new MemoryCacheParams(MAX_MEM,
                MAX_CACHE_SIZE,
                MAX_MEM,
                MAX_CACHE_SIZE,
                MAX_CACHE_SIZE);
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return memoryCacheParams;
            }
        };
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(getApplicationContext())
                        .setBaseDirectoryPath(new File(path))
                        .setBaseDirectoryName(TMP_IMG_DIR)
                        .setMaxCacheSize(50 * ByteConstants.MB)              //默认缓存的最大大小。
                        .setMaxCacheSizeOnLowDiskSpace(20 * ByteConstants.MB)//缓存的最大大小,使用设备时低磁盘空间。
                        .setMaxCacheSizeOnVeryLowDiskSpace(10 * ByteConstants.MB)//缓存的最大大小,当设备极低磁盘空间
                        .build())
                .setProgressiveJpegConfig(new ProgressiveJpegConfig() {
                    @Override
                    public int getNextScanNumberToDecode(int i) {
                        return i + 3;
                    }

                    @Override
                    public QualityInfo getQualityInfo(int i) {
                        boolean isGoodEnough = (i >= 2);
                        return ImmutableQualityInfo.of(i, isGoodEnough, false);
                    }
                })
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setDownsampleEnabled(true)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setResizeAndRotateEnabledForNetwork(true)
                .setNetworkFetcher(new HttpsUrlConnectionNetworkFetcher()) // 增加对Https的支持
                .build();

        Fresco.initialize(this, config);
    }
}
