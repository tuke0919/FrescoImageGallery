package com.yingke.frescoimagegallery.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.yingke.frescoimagegallery.DeviceUtil;
import com.yingke.frescoimagegallery.FrescoUtil;
import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.bean.BaseImageBean;
import com.yingke.frescoimagegallery.bean.ImageBean;

import java.util.ArrayList;


/**
 * 九图控件
 */
public class NineGridLayout extends BaseNineGridLayout<ImageBean> {

    private int MAX_WIDTH = 0;
    private int MIN_WIDTH = 0;
    private int MAX_HEIGHT = 0;
    private int MIN_HEIGHT = 0;

    // 单张图片时 使用默认尺寸
    private boolean mSingleDefaultSize = false;

    public NineGridLayout(Context context) {
        super(context);
        initOther();
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initOther();
    }

    public NineGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initOther();
    }

    public void initOther() {
        MAX_WIDTH = (int) (DeviceUtil.getScreenWidth(mContext) * 2.0f / 3);
        MIN_WIDTH = (int) (DeviceUtil.getScreenWidth(mContext) * 1.0f / 4);

        MAX_HEIGHT = MAX_WIDTH;
        MIN_HEIGHT = MIN_WIDTH;
        Log.d("thumb_image",
                "ScreenWidth x ScreenHeight = " + DeviceUtil.getScreenWidth(mContext) + "x" + DeviceUtil.getScreenHeight(mContext) +" MAX = " + MAX_WIDTH + " MIN = " + MIN_WIDTH);
    }

    /**
     * 设置宽高标准
     * @param maxWidth 最大宽标准
     */
    public void setWidthStandard(int maxWidth) {
        if (maxWidth > DeviceUtil.getScreenWidth(mContext) || maxWidth <= 0) {
            maxWidth = DeviceUtil.getScreenWidth(mContext);
        }
        MAX_WIDTH = (int) (maxWidth * 2.0f / 3);
        MIN_WIDTH = (int) (maxWidth * 1.0f / 4);

        MAX_HEIGHT = MAX_WIDTH;
        MIN_HEIGHT = MIN_WIDTH;
    }

    /**
     * 显示单张图片
     * @param filterImageView
     * @param viewWidth
     * @param viewHeight
     * @param imageBean
     * @return
     */
    @Override
    protected boolean showSingleImage(FilterImageView filterImageView, int viewWidth, int viewHeight, ImageBean imageBean) {
        String oldImageUrl = (String) filterImageView.getTag(R.id.singleImage_thumburl);
        if (imageBean != null) {
            BaseImageBean thumbNail = imageBean.getThumbNail();

            if (thumbNail == null){
                thumbNail = new BaseImageBean();
            }
            Log.d("thumb_image", "originWidth = " + imageBean.getWidth() + " originHeight = " + imageBean.getHeight() + " originUrl = " + imageBean.getImgUrl());
            LayoutParams params = filterImageView.getLayoutParams();
            int requestWidth = params.width;
            int requestHeight = params.height;
//          如果有 裁剪服务器，可对原图片裁剪，压缩质量和分辨率，此处和原图片相同
//            if (thumbNail == null) {
//                // 图片裁剪服务器裁剪
//                String thumbUrl = ImageUtil.getUrl(imageBean.getImgUrl(), requestWidth, requestHeight, 85);
//                Log.d("thumb_image", "requestWidth = " + requestWidth + " requestHeight = " + requestHeight + " thumbUrl = " + thumbUrl);
//                thumbNail = new BaseImageBean();
//                thumbNail.setImgUrl(thumbUrl);
//            }
            thumbNail.setImgUrl(imageBean.getImgUrl());
            thumbNail.setWidth(requestWidth);
            thumbNail.setHeight(requestHeight);
            // 设置数据
            imageBean.setThumbNail(thumbNail);

            // 显示缩略图
            if (!TextUtils.equals(oldImageUrl,thumbNail.getImgUrl() )) {
                String showImageUrl = thumbNail.getImgUrl();
                ResizeOptions resizeOptions = new ResizeOptions(requestWidth, requestHeight);
                FrescoUtil.displayImage(showImageUrl, filterImageView, resizeOptions);
                filterImageView.setTag(R.id.singleImage_thumburl,showImageUrl);
            }
        }
        return false;
    }

    /**
     * 显示多张图片
     * @param filterImageView
     * @param imageUrl
     */
    @Override
    protected void showMultiImage(FilterImageView filterImageView, ImageBean imageUrl) {
        String oldImageUrl = (String) filterImageView.getTag();
        if (imageUrl != null && !TextUtils.equals(oldImageUrl, imageUrl.getImgUrl())) {
            FrescoUtil.displayImage(filterImageView, imageUrl.getImgUrl());
            filterImageView.setTag(imageUrl);
        }
    }

    /**
     * 图片点击
     * @param view
     * @param index
     * @param imageBean
     * @param imageBeans
     */
    @Override
    protected void onImageClicked(View view, int index, ImageBean imageBean, ArrayList<ImageBean> imageBeans) {
        if (mListener != null && !isFastClick()) {
            mListener.onNineGridImageClicked(view,index, imageBean,  imageBeans);
        }
    }

    private static long lastClickTime = 0;
    private static int spaceTime = 1000;

    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isAllowClick;//是否允许点击
        if (currentTime - lastClickTime > spaceTime) {
            isAllowClick = false;
        } else {
            isAllowClick = true;
        }
        lastClickTime = currentTime;
        return isAllowClick;
    }

    /**
     * 计算单张图片显示宽高
     * @param width  图片宽
     * @param height 图片高
     * @return
     */
    @Override
    public int[] getSingleImageViewWidthHeight(int width, int height) {
        if (mSingleDefaultSize || width == 0 || height == 0){
            return super.getSingleImageViewWidthHeight(width,height);
        }
        Log.d("thumb_image", "title = " + getTitle());
        widthHeight = new int[3];
        // 获取状态
        int state = getSingleState(width, height);
        // 宽高比
        float ratio = (width * 1f)/ height;
        Log.d("thumb_image", "state = " + state + " ratio = " + ratio);
        int newWidth = 0;
        int newHeight = 0;
        // 需要裁剪
        // 0 - 不裁剪
        // 1 - 左边缘裁取此时的图片至最宽
        // 2 - 上边缘裁取此时的图片至最高
        int needClip = 0;

        switch (state){
            case 1:
                if (width < height) {
                    int tempWidth1 = (int) (MAX_HEIGHT * ratio);
                    if (tempWidth1 >= MIN_WIDTH) {
                        newWidth = tempWidth1;
                        newHeight = MAX_HEIGHT;
                        needClip = 0;
                    } else {
                        newWidth = MIN_WIDTH;
                        newHeight = MAX_HEIGHT;
                        needClip = 2;
                    }
                } else {
                    int tempHeight1 =(int) (MAX_WIDTH / ratio);
                    if (tempHeight1 >= MIN_HEIGHT) {
                        newWidth = MAX_WIDTH;
                        newHeight = tempHeight1;
                        needClip = 0;
                    } else {
                        newWidth = MAX_WIDTH;
                        newHeight = MIN_HEIGHT;
                        needClip = 1;
                    }
                }
                break;
            case 2:
                int tempHeight = (int) (MAX_WIDTH / ratio);
                if (tempHeight >= MIN_HEIGHT) {
                    newWidth = MAX_WIDTH;
                    newHeight = tempHeight;
                    needClip = 0;
                } else {
                    newWidth = MAX_WIDTH;
                    newHeight = MIN_HEIGHT;
                    needClip = 1;
                }
                break;
            case 3:
                newWidth = MAX_WIDTH;
                newHeight = MIN_HEIGHT;
                needClip = 1;
                break;
            case 4:
                int tempWidth = (int) (MAX_HEIGHT * ratio);
                if (tempWidth >= MIN_WIDTH) {
                    newWidth = tempWidth;
                    newHeight = MAX_HEIGHT;
                    needClip = 0;
                } else {
                    newWidth = MIN_WIDTH;
                    newHeight = MAX_HEIGHT;
                    needClip = 2;
                }
                break;
            case 5:
                newWidth = width;
                newHeight = height;
                needClip = 0;
                break;
            case 6:
                newHeight = MIN_HEIGHT;
                int tempWidth1 = (int) (MIN_HEIGHT * ratio);
                if (tempWidth1 <= MAX_WIDTH) {
                    newWidth = tempWidth1;
                    needClip = 0;
                } else {
                    newWidth = MAX_WIDTH;
                    needClip = 1;
                }
                break;
            case 7:
                newWidth = MIN_WIDTH;
                newHeight = MAX_HEIGHT;
                needClip = 2;
                break;
            case 8:
                newWidth = MIN_WIDTH;
                int tempHeight1 = (int) (MIN_WIDTH / ratio);

                if (tempHeight1 <= MAX_HEIGHT) {
                    newHeight = tempHeight1;
                    needClip = 0;
                } else {
                    newHeight = MAX_HEIGHT;
                    needClip = 2;
                }

                break;
            case 9:
                if (width < height) {
                    newWidth = MIN_WIDTH;
                    int tempHeight2 = (int) (MIN_WIDTH / ratio);
                    if(tempHeight2 <= MAX_HEIGHT) {
                        newHeight = tempHeight2;
                        needClip = 0;
                    } else {
                        newHeight = MAX_HEIGHT;
                        needClip = 2;
                    }

                } else {
                    newHeight = MIN_HEIGHT;
                    int tempWidth2 = (int) (MIN_HEIGHT * ratio);
                    if (tempWidth2 <= MAX_WIDTH) {
                        newWidth = tempWidth2;
                        needClip = 0;
                    } else {
                        newWidth = MAX_WIDTH;
                        needClip = 1;
                    }
                }
                break;
        }
        widthHeight[0] = newWidth;
        widthHeight[1] = newHeight;
        widthHeight[2] = needClip;
        Log.d("thumb_image", "newWidth = " + newWidth + " newHeight = " + newHeight + " needClip = " + needClip);
        return widthHeight;
    }

    /**
     * 单张图片状态
     * @param width  图片的宽
     * @param height 图片的高
     * @return
     */
    private int getSingleState(int width, int height) {
        int state = 0;
        if (width > MAX_WIDTH) {
            if (height > MAX_HEIGHT) {
                state = 1;
            } else if (height >= MIN_HEIGHT) {
                state = 2;
            } else {
                state = 3;
            }
        }
        if (width >= MIN_WIDTH && width <= MAX_WIDTH) {
            if (height > MAX_HEIGHT) {
                state = 4;
            } else if (height >= MIN_HEIGHT) {
                state = 5;
            } else {
                state = 6;
            }
        }

        if (width < MIN_WIDTH) {

            if (height > MAX_HEIGHT) {
                state = 7;
            } else if (height >= MIN_HEIGHT) {
                state = 8;
            } else {
                state = 9;
            }
        }
        return state;
    }
}
