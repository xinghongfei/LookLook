package com.looklook.xinghongfei.looklook.presenter.implPresenter;

import com.looklook.xinghongfei.looklook.api.ApiManage;
import com.looklook.xinghongfei.looklook.bean.news.NewsList;
import com.looklook.xinghongfei.looklook.presenter.INewTopPresenter;
import com.looklook.xinghongfei.looklook.presenter.implView.ITopNewsFragment;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class TopNewsPrensenterImpl extends BasePresenterImpl implements INewTopPresenter {

    ITopNewsFragment mITopNewsFragment;
    public TopNewsPrensenterImpl(ITopNewsFragment iTopNewsFragment){
        mITopNewsFragment=iTopNewsFragment;
    }
    @Override
    public void getNewsList(int t) {
        mITopNewsFragment.showProgressDialog();
        Subscription subscription= ApiManage.getInstence().getTopNewsService().getNews(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        mITopNewsFragment.hidProgressDialog();
                        mITopNewsFragment.showError(e.toString());
                    }

                    @Override
                    public void onNext(NewsList newsList) {
                        mITopNewsFragment.hidProgressDialog();
                        mITopNewsFragment.upListItem(newsList);

                    }
                });
        addSubscription(subscription);
    }
}
