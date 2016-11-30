package com.looklook.xinghongfei.looklook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.adapter.TopNewsAdapter;
import com.looklook.xinghongfei.looklook.bean.news.NewsList;
import com.looklook.xinghongfei.looklook.presenter.implPresenter.TopNewsPrensenterImpl;
import com.looklook.xinghongfei.looklook.presenter.implView.ITopNewsFragment;
import com.looklook.xinghongfei.looklook.view.GridItemDividerDecoration;
import com.looklook.xinghongfei.looklook.widget.WrapContentLinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class TopNewsFragment extends BaseFragment implements ITopNewsFragment {


    boolean loading;
    boolean connected = true;
    TopNewsAdapter mTopNewsAdapter;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;

    int currentIndex;

    TopNewsPrensenterImpl mTopNewsPrensenter;

    @BindView(R.id.recycle_topnews)
    RecyclerView recycle;
    @BindView(R.id.prograss)
    ProgressBar progress;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topnews_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialDate();
        initialView();

    }

    private void initialDate() {

        mTopNewsPrensenter=new TopNewsPrensenterImpl(this);
        mTopNewsAdapter =new TopNewsAdapter(getContext());
    }

    private void initialView() {

        initialListener();
        mLinearLayoutManager = new WrapContentLinearLayoutManager(getContext());
        recycle.setLayoutManager(mLinearLayoutManager);
        recycle.setHasFixedSize(true);
        recycle.addItemDecoration(new GridItemDividerDecoration(getContext(), R.dimen.divider_height, R.color.divider));
        // TODO: 16/8/13 add  animation
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(mTopNewsAdapter);
        recycle.addOnScrollListener(loadingMoreListener);
        if (connected) {
            loadDate();
        }


    }

    private void loadDate() {
        if (mTopNewsAdapter.getItemCount() > 0) {
            mTopNewsAdapter.clearData();
        }
        currentIndex = 0;
        mTopNewsPrensenter.getNewsList(currentIndex);

    }

    private void loadMoreDate() {
        mTopNewsAdapter.loadingStart();
        currentIndex+=20;
        mTopNewsPrensenter.getNewsList(currentIndex);
    }


    private void initialListener() {

        loadingMoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //向下滚动
                {
                    int visibleItemCount = mLinearLayoutManager.getChildCount();
                    int totalItemCount = mLinearLayoutManager.getItemCount();
                    int pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        loadMoreDate();
                    }
                }
            }
        };

    }

    @Override
    public void upListItem(NewsList newsList) {
        loading=false;
        progress.setVisibility(View.INVISIBLE);
        mTopNewsAdapter.addItems(newsList.getNewsList());
    }

    @Override
    public void showProgressDialog() {
        if (currentIndex ==0){
            progress.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void hidProgressDialog() {

        progress.setVisibility(View.INVISIBLE);

    }

    @Override
    public void showError(String error) {
        if (recycle != null) {
            Snackbar.make(recycle, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTopNewsPrensenter.getNewsList(currentIndex);
                }
            }).show();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTopNewsPrensenter.unsubcrible();
//        ButterKnife.reset(this);
    }
}
