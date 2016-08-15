package com.looklook.xinghongfei.looklook.presenter.implView;


import com.looklook.xinghongfei.looklook.bean.guokr.GuokrArticle;
import com.looklook.xinghongfei.looklook.bean.zhihu.ZhihuStory;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public interface IZhihuStory {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);

    void showGuokrArticle(GuokrArticle guokrArticle);
}
