package com.looklook.xinghongfei.looklook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.looklook.xinghongfei.looklook.R;

/**
 * Created by top2015 on 17/6/3.
 */


public class AboutActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        LinearLayout draggable_frame = (LinearLayout) findViewById(R.id.draggable_frame);
        draggable_frame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
    finish();

    }


}
