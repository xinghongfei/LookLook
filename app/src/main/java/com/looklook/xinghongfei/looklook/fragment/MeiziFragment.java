package com.looklook.xinghongfei.looklook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.adapter.MeiziAdapter;
import com.looklook.xinghongfei.looklook.bean.meizi.Gank;
import com.looklook.xinghongfei.looklook.bean.meizi.Meizi;
import com.looklook.xinghongfei.looklook.presenter.implPresenter.MeiziPresenterImpl;
import com.looklook.xinghongfei.looklook.presenter.implView.IMeiziFragment;
import com.looklook.xinghongfei.looklook.util.Once;
import com.looklook.xinghongfei.looklook.widget.WrapContentLinearLayoutManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinghongfei on 16/8/20.
 */
public class MeiziFragment extends BaseFragment implements IMeiziFragment {

    @BindView(R.id.recycle_meizi)
     RecyclerView mRecycleMeizi;
    @BindView(R.id.prograss)
     ProgressBar mPrograss;

    private WrapContentLinearLayoutManager linearLayoutManager;
    private MeiziAdapter meiziAdapter;
    private RecyclerView.OnScrollListener loadmoreListener;
    private MeiziPresenterImpl mMeiziPresenter;

    private boolean isLoading;

    private int index = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meizi_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mMeiziPresenter = new MeiziPresenterImpl(getContext(), this);

        meiziAdapter = new MeiziAdapter(getContext());
        linearLayoutManager = new WrapContentLinearLayoutManager(getContext());

        loadmoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //向下滚动
                {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isLoading = true;
                        index += 1;
                        loadMoreDate();
                    }
                }
            }
        };

        mRecycleMeizi.setLayoutManager(linearLayoutManager);
        mRecycleMeizi.setAdapter(meiziAdapter);
        mRecycleMeizi.addOnScrollListener(loadmoreListener);
        new Once(getContext()).show("tip_guide_6", new Once.OnceCallback() {
            @Override
            public void onOnce() {
                Snackbar.make(mRecycleMeizi, getString(R.string.meizitips), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.meiziaction, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }
        });
        mRecycleMeizi.setItemAnimator(new DefaultItemAnimator());

        loadDate();

        super.onViewCreated(view, savedInstanceState);
    }

    private void loadDate() {
        if (meiziAdapter.getItemCount() > 0) {
            meiziAdapter.clearData();
        }
        mMeiziPresenter.getMeiziData(index);

    }

    private void loadMoreDate() {
        meiziAdapter.loadingStart();
        mMeiziPresenter.getMeiziData(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMeiziPresenter.unsubscrible();
    }

    @Override
    public void updateMeiziData(ArrayList<Meizi> list) {
        meiziAdapter.loadingfinish();
        isLoading = false;
        meiziAdapter.addItems(list);
        mMeiziPresenter.getVedioData(index);
    }

    @Override
    public void updateVedioData(ArrayList<Gank> list) {
        meiziAdapter.addVedioDes(list);
    }

    @Override
    public void showProgressDialog() {
        mPrograss.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        mPrograss.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        mPrograss.setVisibility(View.INVISIBLE);
        if (mRecycleMeizi != null) {
            Snackbar.make(mRecycleMeizi, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMeiziPresenter.getMeiziData(index);
                }
            }).show();

        }
    }
}

