package com.looklook.xinghongfei.looklook.bean.guokr;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 */
public class GuokrHot {
    @SerializedName("result")
    private ArrayList<GuokrHotItem> result = new ArrayList<>();

    public void setResult(ArrayList<GuokrHotItem> result) {
        this.result = result;
    }

    public ArrayList<GuokrHotItem> getResult() {
        return result;
    }

}
