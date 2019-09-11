package com.yingke.frescoimagegallery.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.yingke.frescoimagegallery.DeviceUtil;
import com.yingke.frescoimagegallery.R;
import com.yingke.frescoimagegallery.bean.BaseImageBean;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseNineGridLayout<T extends BaseImageBean> extends ViewGroup {
    private static final String TAG = "BaseNineGridLayout";

    private static final float DEFAULT_SPACING = 3f;
    private static final int MAX_COUNT = 9;

    protected Context mContext;
    // 间隔
    private float mSpacing = DEFAULT_SPACING;
    // 列数
    private int mColumns;
    // 行数
    private int mRows;
    // 总宽
    private int mTotalWidth;
    // 单张图片宽
    private int mSingleWidth;
    // 图片大于9张 是否显示全部
    private boolean mIsShowAll = false;
    // 是否第一次显示
    private boolean mIsFirstShow = true;
    // 是否是共享元素刷新
    private boolean mIsShareElementShow = false;
    // 图片Url数据
    protected ArrayList<T> mImageBeans = new ArrayList();

    // 单张图片的宽高数据
    protected int[] widthHeight;
    // 图片点击
    protected OnNineGridImageClickListener mListener;

    private String title;
    private boolean isClick = true;

    public BaseNineGridLayout(Context context) {
        super(context);
        init(context);
    }

    public BaseNineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseNineGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout);
        mSpacing = typedArray.getDimension(R.styleable.NineGridLayout_sapcing, DEFAULT_SPACING);
        typedArray.recycle();
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        mContext = context;
        if (size() == 0) {
            setVisibility(GONE);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 布局,防止已经显示在界面，重新布局
        if (mIsFirstShow) {
            requestLayoutChanged();
            mIsFirstShow = false;
        } else {
            if (mIsShareElementShow) {
                requestLayoutChanged();
                mIsShareElementShow = false;
            }
        }

    }

    /**
     * 设置是否显示所有图片（超过最大数时）
     *
     * @param isShowAll
     */
    public void setIsShowAll(boolean isShowAll) {
        mIsShowAll = isShowAll;
    }

    /**
     * 设置是否是共享动画 刷新
     *
     * @param mIsShareElementShow
     */
    public void setIsShareElementShow(boolean mIsShareElementShow) {
        this.mIsShareElementShow = mIsShareElementShow;
    }

    /**
     * 设置title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取标题
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置总宽
     *
     * @param mTotalWidth
     */
    public void setTotalWidth(int mTotalWidth) {
        this.mTotalWidth = mTotalWidth;
        Log.e(TAG, "title = " + title + " mTotalWidth = " + mTotalWidth);
    }

    /**
     * 设置 图片Urls
     *
     * @param imageBeans
     */
    public void setImageBeans(List<T> imageBeans) {

        if (imageBeans == null || imageBeans.size() == 0) {
            mImageBeans.clear();
            return;
        }
        setVisibility(VISIBLE);
        mImageBeans.clear();
        mImageBeans.addAll(imageBeans);
    }

    /**
     * 添加布局
     */
    public void requestLayoutChanged() {
        removeAllViews();
        // 图片数量
        int length = size();

        Log.e(TAG, "title = " + title + " length = " + length);
        if (length <= 0) {
            return;
        }
        setVisibility(VISIBLE);
        if (length == 1) {
            T imageUrl = mImageBeans.get(0);
            FilterImageView filterImageView = createImageView(0, imageUrl, true);
            setOneImageViewLayout(filterImageView, imageUrl);
            return;
        } else {
            // 图片数量大于1 布局子View
            for (int index = 0; index < length; index++) {
                T imageUrl = mImageBeans.get(index);
                FilterImageView filterImageView;
                if (!mIsShowAll) {
                    if (index < MAX_COUNT - 1) {
                        // 小于9
                        filterImageView = createImageView(index, imageUrl);
                        setImageViewLayout(filterImageView, index, imageUrl, false);
                    } else {
                        // 第9张
                        if (length <= MAX_COUNT) {
                            // 等于9
                            filterImageView = createImageView(index, imageUrl);
                            setImageViewLayout(filterImageView, index, imageUrl, false);
                        } else {
                            // 大于9
                            filterImageView = createImageView(index, imageUrl);
                            setImageViewLayout(filterImageView, index, imageUrl, true);
                        }
                    }
                } else {
                    filterImageView = createImageView(index, imageUrl);
                    setImageViewLayout(filterImageView, index, imageUrl, false);
                }
            }
        }

    }

    /**
     * 布局 单张图片
     */
    private void setOneImageViewLayout(FilterImageView filterImageView, T imageUrl) {

        // ImageView参数
        LayoutParams params = filterImageView.getLayoutParams();
        int width = params.width;
        int height = params.height;

        // 获取GridView参数
        LayoutParams paramsGrid = getLayoutParams();

        // 获取设置父布局容器 参数
        ViewGroup parent = (ViewGroup) getParent();
        LayoutParams parentParams = parent.getLayoutParams();

        Log.e(TAG, "title = " + title + " isSingle = " + true + " ImageViewHeight = " + height + " GridViewHeight = " + paramsGrid.height + " ParentHeight = " + parentParams.height);

        filterImageView.layout(0, 0, width, height);
        addView(filterImageView);
        // 显示图片
        showSingleImage(filterImageView, width, height, imageUrl);
    }

    /**
     * 多张图片时  计算行数和列数
     *
     * @param length
     */
    public void calculateColumnsRows(int length) {

        if (length <= 3) {
            mRows = 1;
            mColumns = length;
        } else if (length <= 6) {
            mColumns = 3;
            mRows = 2;
            if (length == 4) {
                mColumns = 2;
            }
        } else {
            mColumns = 3;
            if (mIsShowAll) {
                mRows = length / 3;
                int b = length % 3;
                if (b > 0) {
                    mRows++;
                }
            } else {
                mRows = 3;
            }
        }

        Log.e(TAG, "title = " + title + " mRows = " + mRows + " mColumns = " + mColumns);
    }

    /**
     * 多张图片时 设置 网格布局参数
     */
    public void setGridLayoutParams() {
        // 计算行数和列数
        calculateColumnsRows(size());
        // 设置GridView布局参数
        int singleHeight = getSingleWidth();
        int singleWidth = 0;
        if (size() == 0) {
            // 无图片
            singleHeight = 0;
        } else if (size() == 1) {
            // 单张图片
            if (widthHeight == null) {
                widthHeight = getSingleImageViewWidthHeight(mImageBeans.get(0).getWidth(), mImageBeans.get(0).getHeight());
            }
            singleWidth = widthHeight[0];
            singleHeight = widthHeight[1];
        }

        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = (int) (singleHeight * mRows + mSpacing * (mRows - 1));
        setLayoutParams(params);

        Log.e(TAG, "title = " + title + " GridViewHeight = " + params.height);
    }


    /**
     * 计算单张图片时，ImageView的宽高
     *
     * @param width  图片宽
     * @param height 图片高
     * @return 默认写死，子类可重写
     */
    public int[] getSingleImageViewWidthHeight(int width, int height) {
        widthHeight = new int[3];
        widthHeight[0] = DeviceUtil.dip2px(mContext, 240);
        widthHeight[1] = DeviceUtil.dip2px(mContext, 135);
        widthHeight[2] = 0;
        if (width == 0 || height == 0) {
            return widthHeight;
        }
        return widthHeight;
    }


    /**
     * 创建ImageView
     *
     * @param index    位置
     * @param imageUrl 图片Url
     * @return
     */
    private FilterImageView createImageView(final int index, final T imageUrl) {
        return createImageView(index, imageUrl, false);
    }

    /**
     * 创建ImageView
     *
     * @param index         位置
     * @param imageBean     图片Url
     * @param isSingleImage 是单张图
     * @return
     */
    private FilterImageView createImageView(final int index, final T imageBean, boolean isSingleImage) {
        // 计算单张高
        final int singleWidth = getSingleWidth();
        FilterImageView imageView = new FilterImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (!isSingleImage) {
            // 多张图，计算宽和高
            params.width = singleWidth;
            params.height = singleWidth;
        } else {
            // 单张图，计算View宽和高
            if (widthHeight == null) {
                widthHeight = getSingleImageViewWidthHeight(imageBean.getWidth(), imageBean.getHeight());
            }
            params.width = widthHeight[0];
            params.height = widthHeight[1];
            // 是否需要裁剪保存在View上
            imageView.setTag(R.id.singleImage_clip_type, widthHeight[2]);
        }
        imageView.setLayoutParams(params);

        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        if (isSingleImage) {
            // 1,2 两种类型 使用FOCUS_CROP缩放
            int clipType = (int) imageView.getTag(R.id.singleImage_clip_type);
            if (clipType == 1 || clipType == 2) {
                hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
                hierarchy.setActualImageFocusPoint(new PointF(0, 0));
            }
        }
//        hierarchy.setPlaceholderImage(R.drawable.thumb, ScalingUtils.ScaleType.CENTER_CROP);
        imageView.setHierarchy(hierarchy);

        if (isClick) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClicked(v, index, imageBean, mImageBeans);
                }
            });
        }
        return imageView;
    }


    /**
     * 是否设置图片点击事件
     *
     * @param isClick
     */
    public void setImgClickState(boolean isClick) {
        this.isClick = isClick;
    }

    /**
     * 布局ImageView
     *
     * @param filterImageView
     * @param index
     * @param imageUrl
     * @param isShowNumber
     */
    public void setImageViewLayout(FilterImageView filterImageView, int index, T imageUrl, boolean isShowNumber) {

        final int singleWidth = getSingleWidth();
        int singleHeight = singleWidth;

        int[] position = findPosition(index);
        int rowsIndex = position[0];
        int columnsIndex = position[1];

        int left = (int) ((singleWidth + mSpacing) * columnsIndex);
        int top = (int) ((singleHeight + mSpacing) * rowsIndex);
        int right = left + singleWidth;
        int bottom = top + singleHeight;

        // 添加ImageView
        filterImageView.layout(left, top, right, bottom);
        addView(filterImageView);

        Log.e(TAG, "title = " + title + " ImageView = " + index);
        Log.e(TAG, "title = " + title + " left = " + left + " top = " + top + " right = " + right + " bottom = " + bottom);

        if (isShowNumber) {
            //添加超过最大显示数量的文本
            int overCount = size() - MAX_COUNT;
            if (overCount > 0) {
                float textSize = 30;
                final TextView textView = new TextView(mContext);
                textView.setText("+" + String.valueOf(overCount));
                textView.setTextColor(Color.WHITE);
                textView.setPadding(0, singleHeight / 2 - getFontHeight(textSize), 0, 0);
                textView.setTextSize(textSize);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundColor(Color.BLACK);
                textView.getBackground().setAlpha(120);

                textView.layout(left, top, right, bottom);
                addView(textView);
            }
        }
        // 显示图片
        showMultiImage(filterImageView, imageUrl);

    }

    /**
     * 获取字体的高
     *
     * @param fontSize
     * @return
     */
    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 获取 行和列的位置
     *
     * @param childIndex
     * @return
     */
    private int[] findPosition(int childIndex) {
        int[] position = new int[2];
        for (int index = 0; index < mRows; index++) {
            for (int indexJ = 0; indexJ < mColumns; indexJ++) {
                if ((index * mColumns + indexJ) == childIndex) {
                    position[0] = index;
                    position[1] = indexJ;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 计算 多图时 单张的宽和高
     *
     * @return
     */
    private int getSingleWidth() {
        return (int) ((mTotalWidth - mSpacing * (3 - 1)) / 3);
    }


    /**
     * 显示单张图片
     *
     * @param filterImageView
     * @param viewWidth
     * @param viewHeight
     * @param imageUrl
     * @return true 代表按照九宫格默认大小显示，false 代表按照自定义宽高显示
     */
    protected abstract boolean showSingleImage(FilterImageView filterImageView, int viewWidth, int viewHeight, T imageUrl);

    /**
     * 显示图片
     *
     * @param filterImageView
     * @param imageUrl
     */
    protected abstract void showMultiImage(FilterImageView filterImageView, T imageUrl);


    /**
     * 单张图片被点击
     *
     * @param view
     * @param index
     * @param imageUrl
     * @param imageUrls
     */
    protected abstract void onImageClicked(View view, int index, T imageUrl, ArrayList<T> imageUrls);

    /**
     * 图片的数量
     *
     * @return
     */
    public int size() {
        if (mImageBeans == null || mImageBeans.size() == 0) {
            return 0;
        }
        return mImageBeans.size();
    }

    /**
     * 设置图片点击
     *
     * @param mListener
     */
    public void setNineGridImageClickListener(OnNineGridImageClickListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 图片点击监听器
     */
    public interface OnNineGridImageClickListener<Data extends BaseImageBean> {
        /**
         * 图片点击回调
         *
         * @param view
         * @param index
         * @param imageBean
         * @param imageBeans
         */
        public void onNineGridImageClicked(View view, int index, Data imageBean, ArrayList<Data> imageBeans);
    }

}
