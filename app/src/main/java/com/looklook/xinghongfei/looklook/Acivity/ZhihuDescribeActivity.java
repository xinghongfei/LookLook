package com.looklook.xinghongfei.looklook.Acivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.widget.HorizotalTopBottomElasticDragDismissFrameLayout;
import com.looklook.xinghongfei.looklook.widget.ParallaxScrimageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xinghongfei on 16/8/13.
 */
public class ZhihuDescribeActivity extends BaseActivity {
    @InjectView(R.id.shot)
    ParallaxScrimageView mShot;
    @InjectView(R.id.back)
    ImageButton mBack;
    @InjectView(R.id.container)
    FrameLayout mContainer;
    @InjectView(R.id.wv_zhihu)
    WebView mWvZhihu;
    @InjectView(R.id.nest)
    NestedScrollView mNest;
    @InjectView(R.id.container_linear)
    LinearLayout mContainerLinear;
    @InjectView(R.id.draggable_frame)
    HorizotalTopBottomElasticDragDismissFrameLayout mDraggableFrame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihudescribe);
        ButterKnife.inject(this);


    }

    @OnClick({R.id.shot, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shot:
                break;
            case R.id.back:
                break;
        }
    }
}
