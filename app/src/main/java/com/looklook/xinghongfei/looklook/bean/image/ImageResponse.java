package com.looklook.xinghongfei.looklook.bean.image;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 */
public class ImageResponse {
    @SerializedName("data")
    private ImageData data;

    public ImageData getData() {
        return data;
    }

    public void setData(ImageData data) {
        this.data = data;
    }
}
