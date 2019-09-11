package com.yingke.frescoimagegallery.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.yingke.frescoimagegallery.image.NineGridLayout;

import java.util.List;
import java.util.Map;


public class ShareElementHelper {

    // 共享元素动画 - 退出返回数据
    private Bundle mShareExitData;
    // 共享元素动画 - 保存图片父布局
    private ViewGroup mNineGridLayout;

    public ShareElementHelper(){}

    /**
     * 设置转场动画 退出时回调
     */
    public void setExitSharedElementCallback(AppCompatActivity activity) {
        activity.setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                // 进入和退出都回调
                if (mShareExitData != null) {
                    // 返回时调用 - 重新设置共享元素
                    int index = mShareExitData.getInt(GalleryActivity.KEY_SHARE_EXIT_INDEX,0);
                    sharedElements.clear();
                    if (mNineGridLayout != null && mNineGridLayout instanceof NineGridLayout) {
                        // 主要是找到 返回时对应的共享元素
                        View newShareView = mNineGridLayout.getChildAt(index);
                        sharedElements.put(GalleryActivity.KEY_SHARE_ELEMENT_NAME,newShareView);
                        mNineGridLayout = null;
                    }
                    mShareExitData = null;
                }
            }
        });
    }

    public void OnTransitionExitCallback(int resultCode, Intent intent) {
        if (resultCode == GalleryActivity.KEY_SHARE_EXIT_RESULT_CODE) {
            mShareExitData = new Bundle(intent.getExtras());
        }
    }

    /**
     * 保存点击image的父布局
     * @param nineGridLayout
     */
    public void setNineGridLayoutParent(ViewGroup nineGridLayout) {
        mNineGridLayout = nineGridLayout;
    }




}
