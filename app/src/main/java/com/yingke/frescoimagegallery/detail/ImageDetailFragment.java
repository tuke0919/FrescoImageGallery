package com.yingke.frescoimagegallery.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.detail.zoomableview.zoomable.DoubleTapGestureListener;
import com.yingke.frescoimagegallery.detail.zoomableview.zoomable.ZoomableDraweeView;

/**
 * 图片详情的Fragment
 */
public class ImageDetailFragment extends Fragment {

    // 当前位置
    private int position;
    // 图片链接
    private String mImageUrl;
    // 缩放视图
    private ZoomableDraweeView mZoomableDraweeView;

    private View mLoadingView;
    private View mFailedView;

    boolean isNewCreate = false;
    // 是否第一次加载完成，是否可见。
    boolean isVisible = false;

    // 页面监听器
    private IPagerCallback listener;

    public static ImageDetailFragment newInstance(String imageUrl, int position ) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        args.putInt("position", position);
        f.setArguments(args);
        return f;
    }

    /**
     * 设置页面点击监听器
     * @param listener
     */
    public void setPagerClickListener(IPagerCallback listener) {
        this.listener = listener;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisibleToUser) {
            initData();
        } else {
            isNewCreate = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mImageUrl = bundle != null ? bundle.getString("imageUrl", "") : "";
        position = bundle != null ? bundle.getInt("position") : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        View rootview = inflater.inflate(R.layout.item_picture_preview_layout3, container, false);
        // 布局控件
        mZoomableDraweeView = (ZoomableDraweeView) rootview.findViewById(R.id.image_preview_photo_view);
        mZoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
        // needed for double tap to zoom
        mZoomableDraweeView.setIsLongpressEnabled(true);
        // 设置双击 缩放监听
        mZoomableDraweeView.setTapListener(new DoubleTapGestureListener(mZoomableDraweeView){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // 单击
                if (listener != null) {
                    listener.onPagerClicked();
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // 长按
                if (listener != null){
                    listener.onPagerLongPress();
                }
                Toast.makeText(getContext(),"长按点击", Toast.LENGTH_SHORT).show();
                super.onLongPress(e);
            }
        });

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(mImageUrl)
                .build();
        mZoomableDraweeView.setController(controller);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(container.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        mZoomableDraweeView.setHierarchy(hierarchy);
        // 不可下滑
        mZoomableDraweeView.setEnableSwipeDown(false);

        mLoadingView = rootview.findViewById(R.id.image_preview_loading_view);
        mFailedView = rootview.findViewById(R.id.image_preview_load_failed_view);
        mLoadingView.setVisibility(View.GONE);
        mFailedView.setVisibility(View.GONE);
        rootview.setTag(position);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "position = " + position + "");
        super.onActivityCreated(savedInstanceState);
        isNewCreate = true; // 布局新创建
        initData();
    }

    /**
     * 加载数据
     */
    private void initData() {
        if (!isVisible || !isNewCreate) {
            return;
        }
        if (onInitListener != null) {
            onInitListener.onInit();
        }
    }

    /**
     * 初始化 监听器
     * 用于设置当前显示 的View
     */
    public interface OnInitListener {
        void onInit();
    }

    /**
     * 设置
     * @param onInitListener
     */
    public void setOnInitListener(OnInitListener onInitListener) {
        this.onInitListener = onInitListener;
    }

    /**
     *
     */
    private OnInitListener onInitListener;

}
