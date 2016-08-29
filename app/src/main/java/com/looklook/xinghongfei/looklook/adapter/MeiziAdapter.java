package com.looklook.xinghongfei.looklook.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.looklook.xinghongfei.looklook.Activity.MeiziPhotoDescribeActivity;
import com.looklook.xinghongfei.looklook.MainActivity;
import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.bean.meizi.Gank;
import com.looklook.xinghongfei.looklook.bean.meizi.Meizi;
import com.looklook.xinghongfei.looklook.util.DensityUtil;
import com.looklook.xinghongfei.looklook.util.DribbbleTarget;
import com.looklook.xinghongfei.looklook.util.Help;
import com.looklook.xinghongfei.looklook.util.ObservableColorMatrix;
import com.looklook.xinghongfei.looklook.widget.BadgedFourThreeImageView;

import java.util.ArrayList;


public class MeiziAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MainActivity.LoadingMore {

    private static final int TYPE_LOADING_MORE = -1;
    private static final int NOMAL_ITEM = 1;
    boolean showLoadingMore;

    int[] decsi;
    private ArrayList<Meizi> meiziItemes = new ArrayList<>();
    private Context mContext;


    public MeiziAdapter(Context context) {

        this.mContext = context;
        decsi=DensityUtil.getDeviceInfo(mContext);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case NOMAL_ITEM:
                return new MeiziViewHolder(LayoutInflater.from(mContext).inflate(R.layout.meizi_layout_item, parent, false));

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
                bindViewHolderNormal((MeiziViewHolder) holder, position);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHold((LoadingMoreHolder) holder, position);
                break;
        }


    }

    private void bindLoadingViewHold(LoadingMoreHolder holder, int position) {
        holder.progressBar.setVisibility(showLoadingMore? View.VISIBLE : View.INVISIBLE);
    }

    private void bindViewHolderNormal(final MeiziViewHolder holder, final int position) {

        final Meizi meizi = meiziItemes.get(holder.getAdapterPosition());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDescribeActivity(meizi,holder);
            }
        });
//        holder.textView.setText("视频");

//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startDescribeActivity(meizi,holder);
//            }
//        });
        Glide.with(mContext)
                .load(meizi.getUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (!meizi.hasFadedIn) {
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
                                    meizi.hasFadedIn = true;

                                }
                            });
                        }

                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(new DribbbleTarget(holder.imageView, false));


    }

    private void startDescribeActivity(Meizi meizi,RecyclerView.ViewHolder holder){

        Intent intent = new Intent(mContext, MeiziPhotoDescribeActivity.class);
        intent.putExtra("image",meizi.getUrl());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

            final android.support.v4.util.Pair<View, String>[] pairs = Help.createSafeTransitionParticipants
                    ((Activity) mContext, false,new android.support.v4.util.Pair<>(((MeiziViewHolder)holder).imageView, mContext.getString(R.string.meizi)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pairs);
            mContext.startActivity(intent, options.toBundle());
        }else {
            mContext.startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return meiziItemes.size();
    }

    public void addItems(ArrayList<Meizi> list) {
        meiziItemes.addAll(list);
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

        return meiziItemes.size();
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


    public void addVedioDes(ArrayList<Gank> list){

    }

    public void clearData() {
        meiziItemes.clear();
        notifyDataSetChanged();
    }

    public static class LoadingMoreHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView;
        }
    }

    class MeiziViewHolder extends RecyclerView.ViewHolder {
//        final TextView textView;

        BadgedFourThreeImageView imageView;

        MeiziViewHolder(View itemView) {
            super(itemView);
            imageView = (BadgedFourThreeImageView) itemView.findViewById(R.id.item_image_id);
//            textView = (TextView) itemView.findViewById(R.id.item_text_id);

        }
    }


}
