package com.yingke.frescoimagegallery.detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;


import com.yingke.frescoimagegallery.bean.BaseImageBean;

import java.util.ArrayList;
import java.util.List;

public class DragViewPagerAdapter extends FragmentStatePagerAdapter {

    // Pager
    private DragViewPager mViewPager;
    // 页面集合
    private ArrayList<Fragment> mFragments;
    // 页面回调
    private IPagerCallback listener;

    public DragViewPagerAdapter(FragmentManager fm, List<? extends BaseImageBean> imageBeans, DragViewPager viewPager ) {
        super(fm);
        mViewPager = viewPager;
        mFragments = new ArrayList<>();
        updateFragments(imageBeans);
    }
    public DragViewPagerAdapter(FragmentManager fm, DragViewPager viewPager) {
        super(fm);
        mViewPager = viewPager;


    }

    public void setIPagerCallback(IPagerCallback listener) {
        this.listener = listener;
        mViewPager.setIPagerCallback(listener);
        for (Fragment fragment : mFragments){
            if (fragment instanceof ImageDetailFragment) {
                ((ImageDetailFragment) fragment).setPagerClickListener(listener);
            }
        }
    }

    /**
     * 设置数据
     * @param imageBeans
     */
    public void setData(List<? extends BaseImageBean> imageBeans){
        updateFragments(imageBeans);
    }
    /**
     * 创建Fragments
     * @param imageBeans
     */
    public void updateFragments (List<? extends BaseImageBean> imageBeans) {
        final ArrayList<Fragment> fragments = new ArrayList<>();
        if (imageBeans == null) {
            return;
        }
        for (int index = 0; index < imageBeans.size(); index ++) {
            String imageUrl = imageBeans.get(index).getImgUrl();
            final ImageDetailFragment imageFragment = ImageDetailFragment.newInstance(imageUrl, index);
            imageFragment.setPagerClickListener(listener);
            imageFragment.setOnInitListener(new ImageDetailFragment.OnInitListener() {
                @Override
                public void onInit() {
                    View rootView = imageFragment.getView();
                    mViewPager.setCurrentShowView(rootView);
                }
            });
            fragments.add(imageFragment);
        }
        mFragments = new ArrayList<>();
        if (mFragments != null) {
            mFragments.clear();
        }
        mFragments.addAll(fragments);
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

}
