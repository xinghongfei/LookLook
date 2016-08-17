package com.looklook.xinghongfei.looklook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by xinghongfei on 16/8/16.
 */
public class Linear extends LinearLayout {
    public Linear(Context context) {
        super(context);
    }

    public Linear(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Linear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Linear(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }
}
