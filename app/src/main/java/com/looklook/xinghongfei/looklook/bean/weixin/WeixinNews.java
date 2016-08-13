package com.looklook.xinghongfei.looklook.bean.weixin;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/3/4 0004.
 */
public class WeixinNews {
    @SerializedName("ctime")
    private String ctime;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("picUrl")
    private String picUrl;
    @SerializedName("url")
    private String url;

    public String getHottime() {
        return ctime;
    }

    public void setHottime(String hottime) {
        this.ctime = hottime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
