package com.yingke.frescoimagegallery.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseImageBean implements Parcelable {
    public String imgUrl;
    public int height;
    public int width;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgUrl);
        dest.writeInt(this.height);
        dest.writeInt(this.width);
    }

    public BaseImageBean() {
    }

    protected BaseImageBean(Parcel in) {
        this.imgUrl = in.readString();
        this.height = in.readInt();
        this.width = in.readInt();
    }

    public static final Parcelable.Creator<BaseImageBean> CREATOR = new Parcelable.Creator<BaseImageBean>() {
        @Override
        public BaseImageBean createFromParcel(Parcel source) {
            return new BaseImageBean(source);
        }

        @Override
        public BaseImageBean[] newArray(int size) {
            return new BaseImageBean[size];
        }
    };
}
