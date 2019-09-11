package com.yingke.frescoimagegallery.detail;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.bean.ImageBean;

import java.util.ArrayList;

/**
 *
 * viewPager
 */
public class ImagePagerLayout extends FrameLayout  {

    private static final int LAST_INDEX_DEFAULT_VALUE = -1;
    private static final int CURRENT_INDEX_DEFAULT_VALUE = 0;

    // 上下文
    private Context mContext;
    // viewPager
    private DragViewPager mViewPager;
    // 适配器
    private DragViewPagerAdapter mAdapter;
    // 当前位置
    private int mCurrentIndex = CURRENT_INDEX_DEFAULT_VALUE;
    // 页面回调
    private IPagerCallback mIPageCallback;

    private SimpleOnPageChangeListener simpleOnPageChangeListener = new SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

            // 页面选择回调
            if(mIPageCallback != null){
                mIPageCallback.onSelectionChanged(mCurrentIndex, position);
            }
            mCurrentIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public ImagePagerLayout(@NonNull Context context) {
        this(context, null);
    }

    public ImagePagerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImagePagerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initData();
    }

    /**
     * 初始话View
     */
    private void initView(){
        mViewPager = new DragViewPager(getContext());
        mViewPager.setBackgroundColor(Color.BLACK);
        mViewPager.addOnPageChangeListener(simpleOnPageChangeListener);
        addView(mViewPager, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mViewPager.setId(R.id.imagepagerlayout_viewpager_id);
        mAdapter = new DragViewPagerAdapter(((FragmentActivity)getContext()).getSupportFragmentManager(),mViewPager);
        mViewPager.setAdapter(mAdapter);
    }

    /**
     * 设置数据
     * @param imageBeans
     */
    public void setData(ArrayList<ImageBean> imageBeans){
        if(imageBeans != null){
            mAdapter.setData(imageBeans);
            mViewPager.setOffscreenPageLimit(imageBeans.size());
            if(imageBeans.size() > 0 && mIPageCallback != null){
                mIPageCallback.onSelectionChanged(LAST_INDEX_DEFAULT_VALUE, CURRENT_INDEX_DEFAULT_VALUE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置当前页
     * @param position
     */
    public void setCurrentItem(int position){
        if(mAdapter != null && mAdapter.getCount() > 0){
            if(position < 0 || position > mAdapter.getCount()){
                position = 0;
            }
            mViewPager.setCurrentItem(position);
        }
    }

    /**
     * 获取当前 真正的imageview
     * @return
     */
    public View getCurrentImageView() {
        View rooteView = getCurrentShowView();
        if (rooteView != null) {
            return rooteView.findViewById(R.id.image_preview_photo_view);
        }
        return null;
    }

    /**
     * 获取当前显示view
     * @return
     */
    public View getCurrentShowView(){
        return mViewPager.getCurrentShowView();
    }


    /**
     * 设置页面选择监听器
     * @param callback
     */
    public void setIPagerCallback(IPagerCallback callback){
        this.mIPageCallback = callback;
        mAdapter.setIPagerCallback(callback);
    }



}
