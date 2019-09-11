package com.yingke.frescoimagegallery.detail;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;


import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.yingke.frescoimagegallery.DeviceUtil;
import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.detail.zoomableview.zoomable.ZoomableDraweeView;


public class DragViewPager extends ViewPager {

    public static final int STATUS_NORMAL = 0;   //正常浏览状态
    public static final int STATUS_MOVING = 1;   //滑动状态
    public static final int STATUS_RESETTING = 2;//返回中状态
    public static final String TAG = "DragViewPager";


    public static final float MIN_SCALE_SIZE = 0.3f;//最小缩放比例
    public static final int BACK_DURATION = 300;    //ms
    public static final int DRAG_GAP_PX = 50;

    private int currentStatus = STATUS_NORMAL;
    private int currentPageStatus;

    private float mDownX;
    private float mDownY;
    private float screenHeight;

    //要缩放的View
    private View currentShowView;
    // 滑动速度检测类
    private VelocityTracker mVelocityTracker;
    // 页面回调
    private IPagerCallback mIPagerCallback;


    public DragViewPager(Context context) {
        super(context);
        init(context);
    }

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始话
     * @param context
     */
    public void init(Context context) {
        screenHeight = DeviceUtil.getScreenHeight(context);
        setBackgroundColor(Color.BLACK);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                currentPageStatus = state;
            }
        });
    }

    /**
     * 设置当前显示的View
     * @param rootView
     */
    public void setCurrentShowView(View rootView) {
        this.currentShowView = rootView;
    }

    public View getCurrentShowView() {
        return currentShowView;
    }

    // https://blog.csdn.net/yanxiaosa/article/details/52932573
    // https://blog.csdn.net/com314159/article/details/41245329

    private boolean mIsDisallowIntercept = false;
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getAdapter() instanceof DragViewPagerAdapter) {
            // 获取到当前显示View 的 ImageView
            ZoomableDraweeView zoomableDraweeView = (ZoomableDraweeView) currentShowView.findViewById(R.id.image_preview_photo_view);
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getRawX();
                    mDownY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 无缩放时才可滑动
                    if (ev.getPointerCount() == 1 && zoomableDraweeView.getZoomableController().getScaleFactor() == zoomableDraweeView.getZoomableController().getOriginScaleFactor()) {
                        int deltaX = Math.abs((int) (ev.getRawX() - mDownX));
                        int deltaY = (int) (ev.getRawY() - mDownY);
                        // 往下移动超过临界，左右移动不超过临界时，拦截滑动事件
                        if (deltaY > DRAG_GAP_PX && deltaX <= DRAG_GAP_PX) {
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (currentStatus == STATUS_RESETTING) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                addIntoVelocity(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                addIntoVelocity(ev);
                // 下滑Y的距离
                int deltaY = (int) (ev.getRawY() - mDownY);
                // 手指往上滑动
                if (deltaY <= DRAG_GAP_PX && currentStatus != STATUS_MOVING) {
                    return super.onTouchEvent(ev);
                }
                // viewpager不在切换中，并且手指往下滑动，开始缩放
                if (currentPageStatus != SCROLL_STATE_DRAGGING && (deltaY > DRAG_GAP_PX || currentStatus == STATUS_MOVING)) {
                    moveView(ev.getRawX(), ev.getRawY());
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (currentStatus != STATUS_MOVING) {
                    return super.onTouchEvent(ev);
                }

                final float mUpX = ev.getRawX();
                final float mUpY = ev.getRawY();

                // 松开时必须释放VelocityTracker资源
                float yVelocity = computeYVelocity();

                if (yVelocity >= 1200 || Math.abs(mUpY - mDownY) > screenHeight / 4) {
                    // 下滑速度快，或者下滑距离超过屏幕高度的一半，就关闭
                    if (mIPagerCallback != null) {
                        mIPagerCallback.onPictureRelease();
                    }
                } else {
                    // 重回预览状态
                    backToPreviewState(mUpX, mUpY);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 返回浏览状态
     * @param mUpX 手指抬起X
     * @param mUpY 手指抬起Y
     */
    private void backToPreviewState(final float mUpX, final float mUpY) {
        // 设置状态
        currentStatus = STATUS_RESETTING;
        if (mUpY != mDownY) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mUpY, mDownY);
            valueAnimator.setDuration(BACK_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // 计算返回过程中的X，Y，然后moveView
                    float mY = (float) animation.getAnimatedValue();
                    float percent = (mY - mDownY) / (mUpY - mDownY);
                    float mX = percent * (mUpX - mDownX) + mDownX;
                    // 移动View
                    moveView(mX, mY);
                    // 移动结束
                    if (mY == mDownY) {
                        mDownY = 0;
                        mDownX = 0;
                        currentStatus = STATUS_NORMAL;
                    }
                }
            });
            valueAnimator.start();
        } else if (mUpX != mDownX) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mUpX, mDownX);
            valueAnimator.setDuration(BACK_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // 计算返回过程中的X，Y，然后moveView
                    float mX = (float) animation.getAnimatedValue();
                    float percent = (mX - mDownX) / (mUpX - mDownX);
                    float mY = percent * (mUpY - mDownY) + mDownY;
                    // 移动View
                    moveView(mX, mY);
                    // 移动结束
                    if (mX == mDownX) {
                        mDownY = 0;
                        mDownX = 0;
                        currentStatus = STATUS_NORMAL;
                    }
                }
            });
            valueAnimator.start();
        } else if (mIPagerCallback != null){
            mIPagerCallback.onPictureRelease();
        }

    }

    /**
     * 移动View
     * @param movingX 移动中的x
     * @param movingY 移动中的y
     */
    public void moveView(float movingX, float movingY) {
        if (currentShowView == null) {
            return;
        }
        currentStatus = STATUS_MOVING;
        float deltaX = movingX - mDownX;
        float deltaY = movingY - mDownY;
        Log.d("moveview", " movingX = " + movingX + " mDownX = " + mDownX );
        // 缩放比例
        float scale = 1f;
        // 透明度百分比
        float alphaPercent = 1f;
        if (deltaY > 0) {
            scale = 1 - Math.abs(deltaY) / screenHeight;
            alphaPercent = 1 - Math.abs(deltaY) / (screenHeight / 2);
        }
        Log.d("moveview",
                " deltaX = " + deltaX + " deltaY = " + deltaY + " scale = " + scale + " alphaPercent = " + alphaPercent);
        // 当前View平移
        ViewHelper.setTranslationX(currentShowView, deltaX);
        ViewHelper.setTranslationY(currentShowView, deltaY);
        // 缩放View
        scaleView(scale);
        // 设置背景透明度
        setBackgroundColor(getBlackAlpha(alphaPercent));

    }

    /**
     * 缩放View
     * @param scale
     */
    private void scaleView(float scale) {
        scale = Math.min(Math.max(scale, MIN_SCALE_SIZE), 1);
        ViewHelper.setScaleX(currentShowView, scale);
        ViewHelper.setScaleY(currentShowView, scale);
    }

    /**
     * 获取 背景透明度
     * @param percent
     * @return
     */
    private int getBlackAlpha(float percent) {
        percent = Math.min(1, Math.max(0, percent));
        int intAlpha = (int) (percent * 255);
        return Color.argb(intAlpha,0,0,0);
    }

    /**
     * 添加
     * @param event
     */
    private void addIntoVelocity(MotionEvent event) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }


    /**
     * 计算Y速度
     * @return
     */
    private float computeYVelocity() {
        float result = 0;
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000);
            result = mVelocityTracker.getYVelocity();
            releaseVelocity();
        }
        return result;
    }

    /**
     * 释放速度检测
     */
    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setIPagerCallback(IPagerCallback mIPagerCallback) {
        this.mIPagerCallback = mIPagerCallback;
    }
}
