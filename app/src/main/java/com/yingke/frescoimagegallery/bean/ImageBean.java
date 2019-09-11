package com.yingke.frescoimagegallery.bean;

/**
 * 图像数据
 */
public class ImageBean extends BaseImageBean {

    public static final String[] SAMPLE_URIS = {
            "https://images-cn.ssl-images-amazon.com/images/I/81ghN3jk6AL._SL1000_.jpg",
            "https://images-cn.ssl-images-amazon.com/images/I/61YK4KgVWLL._SL1000_.jpg",
            "https://images-cn.ssl-images-amazon.com/images/I/81v6YVUdLnL._SL1000_.jpg",
            "https://images-cn.ssl-images-amazon.com/images/I/61y10jAltmL._SL1000_.jpg",
            "https://images-cn.ssl-images-amazon.com/images/I/71owNXqWERL._SL1000_.jpg",
            "https://images-cn.ssl-images-amazon.com/images/G/28/aplus_rbs/iPhone6PC_170223.jpg",
            "https://images-cn.ssl-images-amazon.com/images/G/28/kindle/2016/zhangr/DPfeature_img/voyag_featureimg._CB532421748_.jpg"
    };

    public static final String[] SAMPLE_URI_SIZES = {
            "1000x1000",
            "1000x1000",
            "1000x1000",
            "1000x1000",
            "1000x1000",
            "750x1520",
            "1280×450"
    };

    // 如果有，可用缩略图
    public BaseImageBean mThumbNail;

    public BaseImageBean getThumbNail() {
        return mThumbNail;
    }

    public void setThumbNail(BaseImageBean mThumbNail) {
        this.mThumbNail = mThumbNail;
    }
}
