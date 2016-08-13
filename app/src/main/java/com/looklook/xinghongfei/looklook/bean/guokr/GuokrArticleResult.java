package com.looklook.xinghongfei.looklook.bean.guokr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 *
 */
public class GuokrArticleResult {
    @SerializedName("small_image")
    private String mSmallImage;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    @SerializedName("content")
    private String content;

    public String getSmallImage() {
        return mSmallImage;
    }

    public void setSmallImage(String smallImage) {
        this.mSmallImage = smallImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
