package com.looklook.xinghongfei.looklook.bean.guokr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 *
 */
public class GuokrHotItem   {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("small_image")
    private String mSmallImage;
    @SerializedName("summary")
    private String summary;
    @SerializedName("date_published")
    private String mTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallImage() {
        return mSmallImage;
    }

    public void setSmallImage(String smallImage) {
        this.mSmallImage = smallImage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTime() {
        return mTime.replace("+08:00","").replace("T"," ");
    }

    public void setTime(String time) {
        mTime = time;
    }
}
