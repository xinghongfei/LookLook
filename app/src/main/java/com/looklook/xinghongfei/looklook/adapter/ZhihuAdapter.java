package com.looklook.xinghongfei.looklook.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.looklook.xinghongfei.looklook.Activity.ZhihuDescribeActivity;
import com.looklook.xinghongfei.looklook.MainActivity;
import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.bean.zhihu.ZhihuDailyItem;
import com.looklook.xinghongfei.looklook.config.Config;
import com.looklook.xinghongfei.looklook.util.DBUtils;
import com.looklook.xinghongfei.looklook.util.DensityUtil;
import com.looklook.xinghongfei.looklook.util.DribbbleTarget;
import com.looklook.xinghongfei.looklook.util.ObservableColorMatrix;
import com.looklook.xinghongfei.looklook.widget.BadgedFourThreeImageView;

import java.util.ArrayList;


public class ZhihuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MainActivity.LoadingMore {

    private static final int TYPE_LOADING_MORE = -1;
    private static final int NOMAL_ITEM = 1;
    boolean showLoadingMore;
    float width;
    int widthPx;
    int heighPx;
    private ArrayList<ZhihuDailyItem> zhihuDailyItems = new ArrayList<>();
    private Context mContext;
    private String mImageUrl;


    public ZhihuAdapter(Context context) {

        this.mContext = context;
        width = mContext.getResources().getDimension(R.dimen.image_width);
        widthPx = DensityUtil.dip2px(mContext, width);
        heighPx = widthPx * 3 / 4;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case NOMAL_ITEM:
                return new ZhihuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.zhihu_layout_item, parent, false));

            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(LayoutInflater.from(mContext).inflate(R.layout.infinite_loading, parent, false));

        }
        return null;

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int type = getItemViewType(position);
        switch (type) {
            case NOMAL_ITEM:
                bindViewHolderNormal((ZhihuViewHolder) holder, position);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHold((LoadingMoreHolder) holder, position);
                break;
        }


    }

    private void bindLoadingViewHold(LoadingMoreHolder holder, int position) {
        holder.progressBar.setVisibility(showLoadingMore == true ? View.VISIBLE : View.INVISIBLE);
    }

    private void bindViewHolderNormal(final ZhihuViewHolder holder, final int position) {

        final ZhihuDailyItem zhihuDailyItem = zhihuDailyItems.get(holder.getAdapterPosition());

        if (DBUtils.getDB(mContext).isRead(Config.ZHIHU, zhihuDailyItem.getId(), 1))
            holder.textView.setTextColor(Color.GRAY);
        else
            holder.textView.setTextColor(Color.BLACK);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goDescribeActivity(holder,zhihuDailyItem);

            }
        });
        holder.textView.setText(zhihuDailyItem.getTitle());
        holder.linearLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goDescribeActivity(holder,zhihuDailyItem);
                    }
                });


        Glide.with(mContext)
                .load(zhihuDailyItems.get(position).getImages()[0])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (!zhihuDailyItem.hasFadedIn) {
                            holder.imageView.setHasTransientState(true);
                            final ObservableColorMatrix cm = new ObservableColorMatrix();
                            final ObjectAnimator animator = ObjectAnimator.ofFloat(cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    holder.imageView.setColorFilter(new ColorMatrixColorFilter(cm));
                                }
                            });
                            animator.setDuration(2000L);
                            animator.setInterpolator(new AccelerateInterpolator());
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.imageView.clearColorFilter();
                                    holder.imageView.setHasTransientState(false);
                                    animator.start();
                                    zhihuDailyItem.hasFadedIn = true;

                                }
                            });
                        }

                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop().override(widthPx, heighPx)
                .into(new DribbbleTarget(holder.imageView, false));


    }

    private void goDescribeActivity(ZhihuViewHolder holder,ZhihuDailyItem zhihuDailyItem){

        DBUtils.getDB(mContext).insertHasRead(Config.ZHIHU, zhihuDailyItem.getId(), 1);
        holder.textView.setTextColor(Color.GRAY);
        Intent intent = new Intent(mContext, ZhihuDescribeActivity.class);
        intent.putExtra("id", zhihuDailyItem.getId());
        intent.putExtra("title", zhihuDailyItem.getTitle());
        intent.putExtra("image",mImageUrl);
//                final android.support.v4.util.Pair<View, String>[] pairs = Help.createSafeTransitionParticipants
//                        ((Activity) mContext, false,new android.support.v4.util.Pair<>(holder.imageView, mContext.getString(R.string.transition_shot)),
//                                new android.support.v4.util.Pair<>(holder.linearLayout, mContext.getString(R.string.transition_shot_background)));
//                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pairs);
//                mContext.startActivity(intent, options.toBundle());
        mContext.startActivity(intent);

    }


    @Override
    public int getItemCount() {
        return zhihuDailyItems.size();
    }


    public void addItems(ArrayList<ZhihuDailyItem> list) {

        int n = list.size();
        zhihuDailyItems.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {

            return NOMAL_ITEM;
        }
        return TYPE_LOADING_MORE;
    }

    private int getDataItemCount() {

        return zhihuDailyItems.size();
    }

    private int getLoadingMoreItemPosition() {
        return showLoadingMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    // TODO: 16/8/13  don't forget call fellow method
    @Override
    public void loadingStart() {
        if (showLoadingMore) return;
        showLoadingMore = true;
        notifyItemInserted(getLoadingMoreItemPosition());
    }

    @Override
    public void loadingfinish() {
        if (!showLoadingMore) return;
        final int loadingPos = getLoadingMoreItemPosition();
        showLoadingMore = false;
        notifyItemRemoved(loadingPos);
    }


    public void clearData() {
        zhihuDailyItems.clear();
        notifyDataSetChanged();
    }

    public static class LoadingMoreHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView;
        }
    }

    class ZhihuViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final LinearLayout linearLayout;
        BadgedFourThreeImageView imageView;

        ZhihuViewHolder(View itemView) {
            super(itemView);
            imageView = (BadgedFourThreeImageView) itemView.findViewById(R.id.item_image_id);
            textView = (TextView) itemView.findViewById(R.id.item_text_id);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.zhihu_item_layout);
        }
    }


}
