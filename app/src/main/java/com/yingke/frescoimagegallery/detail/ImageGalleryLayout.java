package com.yingke.frescoimagegallery.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.bean.ImageBean;
import com.yingke.frescoimagegallery.detail.zoomableview.zoomable.AnimatedZoomableController;
import com.yingke.frescoimagegallery.detail.zoomableview.zoomable.ZoomableController;
import com.yingke.frescoimagegallery.detail.zoomableview.zoomable.ZoomableDraweeView;

import java.util.ArrayList;

import static com.yingke.frescoimagegallery.detail.GalleryActivity.KEY_SHARE_EXIT_INDEX;
import static com.yingke.frescoimagegallery.detail.GalleryActivity.KEY_SHARE_EXIT_RESULT_CODE;
import static com.yingke.frescoimagegallery.detail.zoomableview.zoomable.DefaultZoomableController.LIMIT_ALL;

/**
 * 图像查看器
 */
public class ImageGalleryLayout extends RelativeLayout implements IPagerCallback {
    private static final String INDICATOR_STRING_FORMAT = "%d / %d";

    private Context mContext;
    // 标题
    private RelativeLayout mTitleLayout;
    // 页数指示器
    private TextView mTextViewIndicator;
    // 图片
    private ImagePagerLayout mImagePagerLayout;
    // 图片数据
    private ArrayList<ImageBean> mImageBeans;
    // 当前位置
    private int mCurrentIndex;
    // 页面回调
    private IPagerCallback mIPageCallback;

    public ImageGalleryLayout(Context context) {
        this(context, null);
    }

    public ImageGalleryLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGalleryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    /**
     * 初始话布局
     */
    private void initView(){
        LayoutInflater.from(mContext).inflate(R.layout.image_gallery_layout, this);
        mImagePagerLayout = (ImagePagerLayout) findViewById(R.id.image_pager_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.rl_title_layout);
        mTextViewIndicator = (TextView) findViewById(R.id.tv_indicator);
    }

    /**
     * 获取 Pager布局
     * @return
     */
    public ImagePagerLayout getImagePagerLayout() {
        return mImagePagerLayout;
    }

    /**
     * 设置数据
     * @param data
     * @param currentItem
     */
    public void setData(ArrayList<ImageBean> data, int currentItem){
        setData(data);
        mCurrentIndex = currentItem;
        mImagePagerLayout.setCurrentItem(currentItem);
    }
    /**
     * 设置数据
     * @param data
     */
    public void setData(ArrayList<ImageBean> data){
        mImageBeans = data;
        initViewData();
    }
    private void initViewData(){
        if(mImageBeans != null && mImageBeans.size() > 0){
            mImagePagerLayout.setData(mImageBeans);
            mTextViewIndicator.setText(String.format(INDICATOR_STRING_FORMAT, 1, mImageBeans.size()));
        }
    }

    /**
     * 页面单击
     */
    @Override
    public void onPagerClicked() {
        // 动画关闭
        transitionFinish();
       if (mIPageCallback != null){
           mIPageCallback.onPagerClicked();
       }
    }

    /**
     * 页面长按
     */
    @Override
    public void onPagerLongPress() {
        if (mIPageCallback != null){
            mIPageCallback.onPagerLongPress();
        }
    }

    /**
     * 图片释放
     */
    @Override
    public void onPictureRelease() {
        // 动画关闭
        transitionFinish();
        if (mIPageCallback != null){
            mIPageCallback.onPictureRelease();
        }
    }

    /**
     * 页面选择
     * @param lastIndex
     * @param currentIndex
     */
    @Override
    public void onSelectionChanged(int lastIndex, int currentIndex) {
        mCurrentIndex = currentIndex;
        mTextViewIndicator.setText(String.format(INDICATOR_STRING_FORMAT, currentIndex + 1, mImageBeans.size()));
    }

    /**
     * 转场动画关闭退出
     */
    public void transitionFinish() {
        mTitleLayout.setVisibility(GONE);
        // 缩放返回
        View currentImageView = mImagePagerLayout.getCurrentImageView();
        if (currentImageView != null && currentImageView instanceof ZoomableDraweeView) {
            ZoomableController zoomableController = ((ZoomableDraweeView)currentImageView).getZoomableController();
            if (zoomableController instanceof AnimatedZoomableController && zoomableController.getScaleFactor() > zoomableController.getOriginScaleFactor()) {
                ((AnimatedZoomableController) zoomableController).zoomToPoint(zoomableController.getOriginScaleFactor(),new PointF(0,0),new PointF(0,0),LIMIT_ALL,200,null);
            }
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SHARE_EXIT_INDEX,mCurrentIndex);
        intent.putExtras(bundle);
        ((Activity)mContext).setResult(KEY_SHARE_EXIT_RESULT_CODE,intent);
        ActivityCompat.finishAfterTransition((Activity) mContext);
    }

    /**
     * 设置页面选择监听器
     * @param mIPageCallback
     */
    public void setIPageCallback(IPagerCallback mIPageCallback) {
        this.mIPageCallback = mIPageCallback;
        // 设置回调
        mImagePagerLayout.setIPagerCallback(this);
    }
}
