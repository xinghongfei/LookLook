/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.looklook.xinghongfei.looklook.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.util.AnimUtils;
import com.looklook.xinghongfei.looklook.util.ColorUtils;
import com.looklook.xinghongfei.looklook.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;


public class ElasticDragDismissFrameLayout extends FrameLayout {

    // configurable attribs
    private float dragDismissDistance = Float.MAX_VALUE;
    private float dragDismissFraction = -1f;
    private float dragDismissScale = 1f;
    private boolean shouldScale = false;
    private float dragElacticity = 0.8f;

    // state
    private float totalDrag;
    private boolean draggingDown = false;
    private boolean draggingUp = false;


    float scaleHotizontal;
    float scaleAlpha;

    boolean isHorizontal;
    boolean isRecervory;

    private List<ElasticDragDismissCallback> callbacks;

    public ElasticDragDismissFrameLayout(Context context) {
        this(context, null, 0);
    }

    public ElasticDragDismissFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticDragDismissFrameLayout(Context context, AttributeSet attrs,
                                         int defStyleAttr) {

        super(context,attrs,defStyleAttr);

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ElasticDragDismissFrameLayout, 0, 0);

        if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissDistance)) {
            dragDismissDistance = a.getDimensionPixelSize(R.styleable
                    .ElasticDragDismissFrameLayout_dragDismissDistance, 0);
        } else if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissFraction)) {
            dragDismissFraction = a.getFloat(R.styleable
                    .ElasticDragDismissFrameLayout_dragDismissFraction, dragDismissFraction);
        }
        if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissScale)) {
            dragDismissScale = a.getFloat(R.styleable
                    .ElasticDragDismissFrameLayout_dragDismissScale, dragDismissScale);
            shouldScale = dragDismissScale != 1f;
        }
        if (a.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragElasticity)) {
            dragElacticity = a.getFloat(R.styleable.ElasticDragDismissFrameLayout_dragElasticity,
                    dragElacticity);
        }
        a.recycle();

    }


    public static abstract class ElasticDragDismissCallback {

        /**
         * Called for each drag event.
         *
         * @param elasticOffset       Indicating the drag offset with elasticity applied i.e. may
         *                            exceed 1.
         * @param elasticOffsetPixels The elastically scaled drag distance in pixels.
         * @param rawOffset           Value from [0, 1] indicating the raw drag offset i.e.
         *                            without elasticity applied. A value of 1 indicates that the
         *                            dismiss distance has been reached.
         * @param rawOffsetPixels     The raw distance the user has dragged
         */
        void onDrag(float elasticOffset, float elasticOffsetPixels,
                    float rawOffset, float rawOffsetPixels) { }

        /**
         * Called when dragging is released and has exceeded the threshold dismiss distance.
         */
        void onDragDismissed() { }

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;

        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // if we're in a drag gesture and the user reverses up the we should take those events

        if ( Math.abs(dx)>2*Math.abs(dy)){
            isHorizontal=true;
            isRecervory=true;
            horizontal(dx);

        }


        if (Math.abs(dx)-Math.abs(dy)<3){
            isHorizontal=false;
        }

//
//


        if (draggingDown && dy > 0 || draggingUp && dy < 0) {
            dragScale(dy);
            consumed[1] = dy;
        }
    }



    private void horizontal(int scroll) {
        if (scroll == 0) return;


        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
//        if (scroll < 0 && !draggingUp && !draggingDown) {
//            draggingDown = true;
//            if (shouldScale) setPivotY(getHeight());
//        } else if (scroll > 0 && !draggingDown && !draggingUp) {
//            draggingUp = true;
//            if (shouldScale) setPivotY(0f);
//        }

        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit

        if (isHorizontal&&!draggingDown&&!draggingUp) {
            totalDrag += scroll;
            float dragFraction = (float) Math.log10(1 + (Math.abs(totalDrag) / dragDismissDistance));

            final float scale = 1 - ((1 - dragDismissScale) * dragFraction);
            scaleHotizontal=1-dragFraction;
            if (scaleHotizontal<0.8f){
                scaleHotizontal=0.8f;
            }

            scaleAlpha =1-dragFraction;

            if (scaleHotizontal>0.8f&&scaleHotizontal<1){
                setScaleX(scaleHotizontal);
                setScaleY(scaleHotizontal*1.1f);
                if (scaleAlpha>0.8f&&scaleAlpha<1.0f){
                    setAlpha(scaleAlpha);
                }
            }



//            float dragTo = dragFraction * dragDismissDistance * dragElacticity;
//            dispatchDragCallback(dragFraction, dragTo,
//                    Math.min(1f, Math.abs(totalDrag) / dragDismissDistance), totalDrag);

        }


        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
//        if (!isHorizontal) {
//            setScaleX(1f);
//            setScaleY(1f);
//        }


    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {

//        Log.d("maat","dyUnconsume:"+dyUnconsumed+"dyConsumed"+dyConsumed+"+"+"dUncomsumed:"+dxConsumed+"dxConsumed"+dxConsumed);

//        dragScale(dyUnconsumed);
    }

    @Override
    public void onStopNestedScroll(View child) {
        if (!isHorizontal&&Math.abs(totalDrag)<=dragDismissDistance){
            if (isRecervory){
                // TODO: 16/8/15 recovery

                totalDrag = 0;
                draggingDown = draggingUp = false;
                setTranslationY(0f);
                setScaleX(1f);
                setScaleY(1f);
                setAlpha(1f);



                isHorizontal=false;
            }
        }


        if (Math.abs(totalDrag) >= dragDismissDistance) {
            dispatchDismissCallback();
        } else { // settle back to natural position
            animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f).alpha(1f)
                    .setDuration(200L)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(getContext()))
                    .setListener(null)
                    .start();
            totalDrag = 0;
            draggingDown = draggingUp = false;
            dispatchDragCallback(0f, 0f, 0f, 0f);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction;
        }
    }

    public void addListener(ElasticDragDismissCallback listener) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(listener);
    }

    public void removeListener(ElasticDragDismissCallback listener) {
        if (callbacks != null && callbacks.size() > 0) {
            callbacks.remove(listener);
        }
    }

    private void dragScale(int scroll) {
        if (scroll == 0) return;

        totalDrag += scroll;

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true;
            if (shouldScale) setPivotY(getHeight());
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true;
            if (shouldScale) setPivotY(0f);
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        float dragFraction = (float) Math.log10(1 + (Math.abs(totalDrag) / dragDismissDistance));

        // calculate the desired translation given the drag fraction
        float dragTo = dragFraction * dragDismissDistance * dragElacticity;

        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1;
        }
        setTranslationY(dragTo);

        if (shouldScale) {
            final float scale = 1 - ((1 - dragDismissScale) * dragFraction);
            setScaleX(scale);
            setScaleY(scale);
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        if ((draggingDown && totalDrag >= 0)
                || (draggingUp && totalDrag <= 0)) {
            totalDrag = dragTo = dragFraction = 0;
            draggingDown = draggingUp = false;
            setTranslationY(0f);
            setScaleX(1f);
            setScaleY(1f);
        }
        dispatchDragCallback(dragFraction, dragTo,
                Math.min(1f, Math.abs(totalDrag) / dragDismissDistance), totalDrag);
    }

    private void dispatchDragCallback(float elasticOffset, float elasticOffsetPixels,
                                      float rawOffset, float rawOffsetPixels) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                callback.onDrag(elasticOffset, elasticOffsetPixels,
                        rawOffset, rawOffsetPixels);
            }
        }
    }

    private void dispatchDismissCallback() {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (ElasticDragDismissCallback callback : callbacks) {
                callback.onDragDismissed();
            }
        }
    }

    /**
     * An {@link ElasticDragDismissCallback} which fades system chrome (i.e. status bar and
     * navigation bar) whilst elastic drags are performed and
     * {@link Activity#finishAfterTransition() finishes} the activity when drag dismissed.
     */
    public static class SystemChromeFader extends ElasticDragDismissCallback {

        private final Activity activity;
        private  int statusBarAlpha;
        private  int navBarAlpha;
        private final boolean fadeNavBar;

        public SystemChromeFader(Activity activity) {
            this.activity = activity;
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                statusBarAlpha = Color.alpha(activity.getWindow().getStatusBarColor());
                navBarAlpha = Color.alpha(activity.getWindow().getNavigationBarColor());
            }

            fadeNavBar = ViewUtils.isNavBarOnBottom(activity);
        }

        @Override
        public void onDrag(float elasticOffset, float elasticOffsetPixels,
                           float rawOffset, float rawOffsetPixels) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {

                if (elasticOffsetPixels > 0) {
                    // dragging downward, fade the status bar in proportion

                    activity.getWindow().setStatusBarColor(ColorUtils.modifyAlpha(activity.getWindow()
                            .getStatusBarColor(), (int) ((1f - rawOffset) * statusBarAlpha)));

                } else if (elasticOffsetPixels == 0) {
                    // reset
                    activity.getWindow().setStatusBarColor(ColorUtils.modifyAlpha(
                            activity.getWindow().getStatusBarColor(), statusBarAlpha));
                    activity.getWindow().setNavigationBarColor(ColorUtils.modifyAlpha(
                            activity.getWindow().getNavigationBarColor(), navBarAlpha));
                } else if (fadeNavBar) {
                    // dragging upward, fade the navigation bar in proportion
                    activity.getWindow().setNavigationBarColor(
                            ColorUtils.modifyAlpha(activity.getWindow().getNavigationBarColor(),
                                    (int) ((1f - rawOffset) * navBarAlpha)));
                }
            }
        }

        public void onDragDismissed() {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                activity.finishAfterTransition();

            }else {
                activity.finish();
            }
        }
    }

}
