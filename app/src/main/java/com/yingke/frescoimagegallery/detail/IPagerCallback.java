package com.yingke.frescoimagegallery.detail;

/**
 * 页面回调
 */
public interface IPagerCallback {

    /**
     * 单击
     */
    void onPagerClicked();

    /**
     * 长按
     */
    void onPagerLongPress();

    /**
     * 图片释放
     */
    void onPictureRelease();

    /**
     * 页面选择改变
     */
    void onSelectionChanged(int lastIndex, int currentIndex);
}
