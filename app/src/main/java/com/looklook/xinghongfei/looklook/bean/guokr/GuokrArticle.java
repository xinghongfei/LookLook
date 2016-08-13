package com.looklook.xinghongfei.looklook.bean.guokr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 */
public class GuokrArticle {
    @SerializedName("result")
    private GuokrArticleResult result;

    public GuokrArticleResult getResult() {
        return result;
    }

    public void setResult(GuokrArticleResult result) {
        this.result = result;
    }
}
