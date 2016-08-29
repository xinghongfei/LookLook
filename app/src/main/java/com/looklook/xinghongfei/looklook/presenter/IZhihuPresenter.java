package com.looklook.xinghongfei.looklook.presenter;



public interface IZhihuPresenter extends BasePresenter{
    void getLastZhihuNews();

    void getTheDaily(String date);

    void getLastFromCache();
}
