package com.yingke.frescoimagegallery;

import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoUtil {

    /**
     * 显示图片 裁剪
     * @param path
     * @param draweeView
     * @param resizeOptions
     */
    public static void displayImage(String path, SimpleDraweeView draweeView, ResizeOptions resizeOptions) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(path))
                .setResizeOptions(resizeOptions)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }


    /**
     * 显示图片，支持gif,
     * 不裁剪
     * @param imageView
     * @param url
     */
    public static void displayImage(SimpleDraweeView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(params.width, params.height))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(imageView.getController())
                    .build();

//            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
//                            .setUri(uri)
//                            .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
//                            .build();
            imageView.setController(controller);
        } else {
            imageView.setImageURI(Uri.parse(""));
        }
    }



}
