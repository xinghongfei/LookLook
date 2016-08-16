package com.looklook.xinghongfei.looklook.Acivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.bean.guokr.GuokrArticle;
import com.looklook.xinghongfei.looklook.bean.zhihu.ZhihuStory;
import com.looklook.xinghongfei.looklook.config.Config;
import com.looklook.xinghongfei.looklook.presenter.IZhihuStoryPresenter;
import com.looklook.xinghongfei.looklook.presenter.implPresenter.ZhihuStoryPresenterImpl;
import com.looklook.xinghongfei.looklook.presenter.implView.IZhihuStory;
import com.looklook.xinghongfei.looklook.util.ImageLoader;
import com.looklook.xinghongfei.looklook.util.WebUtil;
import com.looklook.xinghongfei.looklook.widget.HorizotalTopBottomElasticDragDismissFrameLayout;
import com.looklook.xinghongfei.looklook.widget.ParallaxScrimageView;

import java.lang.reflect.InvocationTargetException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xinghongfei on 16/8/13.
 */
public class ZhihuDescribeActivity extends BaseActivity implements IZhihuStory {

    @InjectView(R.id.shot)
    ParallaxScrimageView mShot;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.container)
    FrameLayout mContainer;
    @InjectView(R.id.wv_zhihu)
    WebView wvZhihu;
    @InjectView(R.id.nest)
    NestedScrollView mNest;
    @InjectView(R.id.container_linear)
    LinearLayout mContainerLinear;
    @InjectView(R.id.draggable_frame)
    HorizotalTopBottomElasticDragDismissFrameLayout mDraggableFrame;

    private int type;
    private String id;
    private String title;
    private String url;
    private String mImageUrl;
    private IZhihuStoryPresenter mIZhihuStoryPresenter;
    private HorizotalTopBottomElasticDragDismissFrameLayout.SystemChromeFader chromeFader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihudescribe);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
//      mToolbar.setLogo(R.drawable.ic_arrow_back);
        initData();
        initView();
        getData();

        chromeFader = new HorizotalTopBottomElasticDragDismissFrameLayout.SystemChromeFader(this);


    }


    private void initData() {
//        type = getIntent().getIntExtra("type", 0);
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        mImageUrl=getIntent().getStringExtra("image");
        mIZhihuStoryPresenter = new ZhihuStoryPresenterImpl(this);
    }

    private void initView() {
        mToolbar.setTitle(title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZhihuDescribeActivity.this.onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        WebSettings settings = wvZhihu.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        //settings.setUseWideViewPort(true);造成文字太小
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wvZhihu.setWebChromeClient(new WebChromeClient());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDraggableFrame.addListener(chromeFader);
        try {
            wvZhihu.getClass().getMethod("onResume").invoke(wvZhihu, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDraggableFrame.removeListener(chromeFader);
        try {
            wvZhihu.getClass().getMethod("onPause").invoke(wvZhihu, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        //webview内存泄露
        if (wvZhihu != null) {
            ((ViewGroup) wvZhihu.getParent()).removeView(wvZhihu);
            wvZhihu.destroy();
            wvZhihu = null;
        }
        mIZhihuStoryPresenter.unsubcrible();
        super.onDestroy();

    }

    private void getData() {

            mIZhihuStoryPresenter.getZhihuStory(id);

    }
    @OnClick(R.id.shot)
    public void onClick() {
        mNest.smoothScrollTo(0, 0);

    }

    @Override
    public void showError(String error) {
        Snackbar.make(wvZhihu, getString(R.string.snack_infor), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        }).show();
    }

    @Override
    public void showZhihuStory(ZhihuStory zhihuStory) {
        ImageLoader.loadImage(ZhihuDescribeActivity.this,zhihuStory.getImage(),mShot);
        url = zhihuStory.getShareUrl();
        if (TextUtils.isEmpty(zhihuStory.getBody())) {
            wvZhihu.loadUrl(zhihuStory.getShareUrl());
        } else {
            String data = WebUtil.buildHtmlWithCss(zhihuStory.getBody(), zhihuStory.getCss(), Config.isNight);
            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
        }
    }

    @Override
    public void showGuokrArticle(GuokrArticle guokrArticle) {

    }
}
