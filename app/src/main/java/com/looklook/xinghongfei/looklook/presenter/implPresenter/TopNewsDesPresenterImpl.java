package com.looklook.xinghongfei.looklook.presenter.implPresenter;


import com.looklook.xinghongfei.looklook.bean.news.NewsDetailBean;
import com.looklook.xinghongfei.looklook.presenter.INewTopDescriblePresenter;
import com.looklook.xinghongfei.looklook.presenter.implView.ITopNewsDesFragment;
import com.looklook.xinghongfei.looklook.util.NewsJsonUtils;
import com.looklook.xinghongfei.looklook.util.OkHttpUtils;
import com.looklook.xinghongfei.looklook.util.Urls;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class TopNewsDesPresenterImpl extends BasePresenterImpl implements INewTopDescriblePresenter {

    private ITopNewsDesFragment mITopNewsFragment;

    public TopNewsDesPresenterImpl(ITopNewsDesFragment topNewsFragment) {
        if (topNewsFragment == null)
            throw new IllegalArgumentException(" must not be null");
        mITopNewsFragment = topNewsFragment;
    }
    private String getDetailUrl(String docId) {
        StringBuffer sb = new StringBuffer(Urls.NEW_DETAIL);
        sb.append(docId).append(Urls.END_DETAIL_URL);
        return sb.toString();
    }

    @Override
    public void getDescrible(final String docid) {
        mITopNewsFragment.showProgressDialog();
        String url = getDetailUrl(docid);
        OkHttpUtils.ResultCallback<String> loadNewsCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                NewsDetailBean newsDetailBean = NewsJsonUtils.readJsonNewsDetailBeans(response, docid);
               mITopNewsFragment.upListItem(newsDetailBean);
            }

            @Override
            public void onFailure(Exception e) {
                mITopNewsFragment.showError(e.toString());
            }
        };
        OkHttpUtils.get(url, loadNewsCallback);

    }
}
