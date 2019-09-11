package com.yingke.frescoimagegallery.image;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 带触感反馈的 ImageView
 */
public class FilterImageView extends SimpleDraweeView {
    // 开关
    private boolean touchEffect = false;
    public final float[] BG_PRESSED = new float[] { 1, 0, 0, 0, -50, 0, 1,
            0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
    public final float[] BG_NOT_PRESSED = new float[] { 1, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

    public FilterImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FilterImageView(Context context) {
        super(context);
    }

    public FilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FilterImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    @Override
    public void setPressed(boolean pressed) {
        updateView(pressed);
        super.setPressed(pressed);
    }

    /**
     * 根据是否按下去来刷新bg和src
     * @param pressed
     */
    private void updateView(boolean pressed){
        //如果没有点击效果
        if( !touchEffect ){
            return;
        }
        if(pressed){
            // 点击 通过设置滤镜来改变图片亮度
            this.setDrawingCacheEnabled(true);
            this.setColorFilter(new ColorMatrixColorFilter(BG_PRESSED)) ;
            Drawable drawable = getDrawable();
            if (drawable != null) {
                drawable.setColorFilter( new ColorMatrixColorFilter(BG_PRESSED));
            }

        }else{
            //未点击
            this.setColorFilter(new ColorMatrixColorFilter(BG_NOT_PRESSED));
            Drawable drawable = getDrawable();
            if (drawable != null) {
                drawable.setColorFilter( new ColorMatrixColorFilter(BG_NOT_PRESSED));
            }
        }
    }

    /**
     * 设置ColorFilter
     */
    public void setFilter() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            drawable = getBackground();
        }
        if (drawable != null) {
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * 清除ColorFilter
     */
    private void removeFilter() {

        Drawable drawable = getDrawable();
        //当src图片为Null，获取背景图片
        if (drawable==null) {
            drawable = getBackground();
        }
        if(drawable != null){
            //清除滤镜
            drawable.clearColorFilter();
        }
    }
}
