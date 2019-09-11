package com.yingke.frescoimagegallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.frescoimagegallery.bean.GalleryBean;
import com.yingke.frescoimagegallery.bean.ImageBean;
import com.yingke.frescoimagegallery.detail.GalleryActivity;
import com.yingke.frescoimagegallery.detail.ShareElementHelper;
import com.yingke.frescoimagegallery.image.BaseNineGridLayout;
import com.yingke.frescoimagegallery.image.NineGridLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<GalleryBean> mGalleryBeans;

    private ShareElementHelper mShareElementHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置Android 6.0 以上是亮色模式
        StatusBarLightMode(this, true);
        // 设置状态栏 背景色
        setTransStatusBar(this, true);

        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerview);
        mGalleryBeans =  GalleryBean.getDatas();
        mRecyclerViewAdapter = new RecyclerViewAdapter(new WeakReference<Context>(this),mGalleryBeans);
        // 设置图片点击监听器
        mRecyclerViewAdapter.setListener(new BaseNineGridLayout.OnNineGridImageClickListener<ImageBean>() {
            @Override
            public void onNineGridImageClicked(View view, int index, ImageBean imageBean, ArrayList<ImageBean> imageBeans) {
                if (imageBean != null && !TextUtils.isEmpty(imageBean.getImgUrl())) {
                    // 保存 点击图片 父布局
                    setNineGridLayoutParent((ViewGroup) view.getParent());
                    GalleryActivity.start(MainActivity.this,index,imageBeans,view);
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.notifyDataSetChanged();

        // 设置共享元素回调
        mShareElementHelper = new ShareElementHelper();
        mShareElementHelper.setExitSharedElementCallback(this);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (mShareElementHelper != null){
            mShareElementHelper.OnTransitionExitCallback(resultCode,data);
        }
    }

    /**
     * 保存点击image的父布局
     * @param nineGridLayout
     */
    public void setNineGridLayoutParent(ViewGroup nineGridLayout) {
       if (mShareElementHelper != null){
           mShareElementHelper.setNineGridLayoutParent(nineGridLayout);
       }
    }


    /**
     * 适配器
     */
    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private WeakReference<Context> mContext;
        private List<GalleryBean> galleryBeans;
        private BaseNineGridLayout.OnNineGridImageClickListener<ImageBean> listener;

        public RecyclerViewAdapter(WeakReference<Context> mContext, List<GalleryBean> galleryBeans) {
            this.mContext = mContext;
            this.galleryBeans = galleryBeans;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView  = LayoutInflater.from(mContext.get()).inflate(R.layout.item_main_layout, parent, false);
            return new ImageViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
           if (holder instanceof ImageViewHolder){
               ((ImageViewHolder) holder).onRefreshData(position, galleryBeans.get(position));
           }
        }

        @Override
        public int getItemCount() {
            return galleryBeans == null ? 0 : galleryBeans.size();
        }

        public void setListener(BaseNineGridLayout.OnNineGridImageClickListener<ImageBean> listener) {
            this.listener = listener;
        }

        /**
         * Holder
         */
        public class ImageViewHolder extends RecyclerView.ViewHolder implements  BaseNineGridLayout.OnNineGridImageClickListener<ImageBean>{
            private View mRootView;
            private SimpleDraweeView mAvatarImage;
            private TextView mNickName;
            private TextView mDescription;
            private TextView mCreateTime;
            private LinearLayout mImageContainer;

            public ImageViewHolder(View itemView) {
                super(itemView);
                mRootView = itemView;
                mAvatarImage = mRootView.findViewById(R.id.image_item_avatar);
                mNickName = mRootView.findViewById(R.id.image_item_nickname);
                mDescription = mRootView.findViewById(R.id.image_item_description);
                mCreateTime = mRootView.findViewById(R.id.image_item_createtime);
                mImageContainer = mRootView.findViewById(R.id.image_item_container);
            }

            public void onRefreshData(int position, GalleryBean galleryBean){
                if (galleryBean == null) {
                    return;
                }
                FrescoUtil.displayImage(mAvatarImage, "res://xxx/" + galleryBean.getAvatar());
                mNickName.setText(galleryBean.getNickname());
                mDescription.setText(galleryBean.getDescription());
                mCreateTime.setText(galleryBean.getCreatetime());
                // 内置图片集合
                mImageContainer.removeAllViews();
                if (galleryBean.getImageList() != null && galleryBean.getImageList().size() >= 0) {
                    // 添加图片集合
                    NineGridLayout mNineGridLayout  = new NineGridLayout(mContext.get());
                    mNineGridLayout.setTag(position);
                    mNineGridLayout.setNineGridImageClickListener(this);
                    // 设置图片数据
                    mNineGridLayout.setTitle(galleryBean.getNickname());
                    ArrayList<ImageBean> imageBeans = new ArrayList<>();
                    imageBeans.addAll(galleryBean.getImageList());
                    // 先设置数据
                    mNineGridLayout.setImageBeans(imageBeans);
                    // 设置GridView布局参数
                    mNineGridLayout.setTotalWidth(DeviceUtil.getScreenWidth(mContext.get()) - DeviceUtil.dip2px(mContext.get(), 92));
                    mNineGridLayout.setGridLayoutParams();
                    // 设置父布局参数
                    ViewGroup.LayoutParams parentParams = mImageContainer.getLayoutParams();
                    parentParams.height = mNineGridLayout.getLayoutParams().height;
                    mImageContainer.addView(mNineGridLayout);
                }
            }

            @Override
            public void onNineGridImageClicked(View view, int index, ImageBean imageBean, ArrayList<ImageBean> imageBeans) {
                     if (listener != null){
                         listener.onNineGridImageClicked(view,index, imageBean, imageBeans);
                     }
            }
        }
    }

    /**
     * 透明状态栏
     * @param activity
     * @param isLightStatusBar 是否是状态栏黑色字体图标
     */
    public  void setTransStatusBar(Activity activity, boolean isLightStatusBar){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (isLightStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 5.0以上可设置状态栏背景
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配6.0以上版本其他Android
     * @param activity
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return
     */
    public  int StatusBarLightMode(Activity activity, boolean dark) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            result = 3;
        }
        return result;
    }



}
