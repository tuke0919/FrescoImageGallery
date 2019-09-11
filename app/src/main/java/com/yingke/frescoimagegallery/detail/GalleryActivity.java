package com.yingke.frescoimagegallery.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.SharedElementCallback;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.bean.ImageBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查看 图片详情
 */
public class GalleryActivity extends FragmentActivity {

    // 数据KEY
    private static String KEY_CURRENT_POSITION = "KEY_CURRENT_POSITION";
    private static String KEY_IMAGE_LIST = "KEY_IMAGE_LIST";

    // 共享元素 View
    public static String KEY_SHARE_ELEMENT_NAME = "TRANSITION_VIEW";
    // 共享动画 退出图片索引
    public static String KEY_SHARE_EXIT_INDEX = "IMAGE_INDEX";
    // 共享动画 退出结果码
    public static int KEY_SHARE_EXIT_RESULT_CODE = 100;
    // 共享视图
    public static View mShareView;


    // 图片相册布局
    private ImageGalleryLayout mImageGalleryLayout;
    // 图片数据
    private ArrayList<ImageBean> mImageBeans;
    // 当前位置
    private int mPosition;


    public static void start(Activity context, ImageBean imageUrl, View shareView) {
        ArrayList<ImageBean> imageUrls = new ArrayList<>();
        imageUrls.add(imageUrl);
        start(context, 0, imageUrls, shareView);
    }

    public static void start(Activity context, int currentPosition, List<ImageBean> imageUrls, View shareView) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(KEY_CURRENT_POSITION, currentPosition);
        intent.putParcelableArrayListExtra(KEY_IMAGE_LIST, (ArrayList<ImageBean>) imageUrls);
        // 共享元素跳转
        mShareView = shareView;
        ActivityOptionsCompat optionsCompat = null;
        if (shareView == null) {
            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context);
        } else {
            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, shareView, KEY_SHARE_ELEMENT_NAME);
        }
        ActivityCompat.startActivity(context,intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置使用分享元素
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_image_gallery_layout);
        // 实例化
        mImageGalleryLayout = (ImageGalleryLayout) findViewById(R.id.image_gallery_layout);

        mImageBeans= getIntent().getParcelableArrayListExtra(KEY_IMAGE_LIST);
        mPosition = getIntent().getIntExtra(KEY_CURRENT_POSITION, 0);

        mImageGalleryLayout.setData(mImageBeans,mPosition);

        mImageGalleryLayout.setIPageCallback(new IPagerCallback() {
            @Override
            public void onPagerClicked() {

            }

            @Override
            public void onPagerLongPress() {
                // 长按，显示下载弹窗，分享面板等


            }

            @Override
            public void onPictureRelease() {

            }

            @Override
            public void onSelectionChanged(int lastIndex, int currentIndex) {

            }
        });

        // 设置共享元素 进入 和 退出 回调
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // 进入和退出时都回调
                View view = mImageGalleryLayout.getImagePagerLayout().getCurrentShowView();
                sharedElements.clear();
                sharedElements.put(KEY_SHARE_ELEMENT_NAME, view);
            }
        });
        // 设置Fresco和共享元素的动画，无此Fresco无法显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransition =  DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER);
            Transition returnTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP);
            if (mShareView != null) {
                // 单张图片时
                if (mImageBeans.size() == 1) {
                    // 1,2 两种类型 使用FOCUS_CROP缩放
                    int clipType = (int) mShareView.getTag(R.id.singleImage_clip_type);
                    if (clipType == 1 || clipType == 2) {
                        enterTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FOCUS_CROP, ScalingUtils.ScaleType.FIT_CENTER, new PointF(0,0),null);
                        returnTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.FOCUS_CROP,null,new PointF(0,0));
                    }
                }
            }
            enterTransition.setDuration(250);
            enterTransition.setInterpolator(new AccelerateInterpolator());
            returnTransition.setDuration(250);
            enterTransition.setInterpolator(new DecelerateInterpolator());
            getWindow().setSharedElementEnterTransition(enterTransition);   // 进入
            getWindow().setSharedElementReturnTransition(returnTransition); // 返回
        }
    }

    @Override
    public void onBackPressed() {
        mImageGalleryLayout.transitionFinish();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("onWindowFocusChanged", "hasFocus = " + hasFocus);
    }
}