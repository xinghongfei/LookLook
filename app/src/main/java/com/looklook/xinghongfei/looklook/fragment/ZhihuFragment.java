package com.looklook.xinghongfei.looklook.fragment;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.adapter.ZhihuAdapter;
import com.looklook.xinghongfei.looklook.bean.zhihu.ZhihuDaily;
import com.looklook.xinghongfei.looklook.presenter.implPresenter.ZhihuPresenterImpl;
import com.looklook.xinghongfei.looklook.presenter.implView.IZhihuFragment;
import com.looklook.xinghongfei.looklook.view.GridItemDividerDecoration;
import com.looklook.xinghongfei.looklook.widget.WrapContentLinearLayoutManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class ZhihuFragment extends BaseFragment implements IZhihuFragment {

    ImageView noConnection;
    TextView noConnectionText;
    boolean loading;
    ZhihuAdapter zhihuAdapter;
    boolean connected = true;
    boolean monitoringConnectivity;

    float toolbarArlp = 100;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;
    RecyclerView.OnScrollListener tooldimissListener;


    ZhihuPresenterImpl zhihuPresenter;
    @InjectView(R.id.recycle_zhihu)
    RecyclerView recycle;
    @InjectView(R.id.prograss)
    ProgressBar progress;

    private String currentLoadDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.zhihu_fragment_layout, container, false);
//        checkConnectivity(view);
        ButterKnife.inject(this, view);
        return view;

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialDate();
        initialView();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zhihuPresenter.unsubcrible();

    }


    private void initialDate() {
        zhihuPresenter = new ZhihuPresenterImpl(getContext(),this);
        zhihuAdapter = new ZhihuAdapter(getContext());
    }

    private void initialView() {

        initialListener();

        mLinearLayoutManager = new WrapContentLinearLayoutManager(getContext());
        recycle.setLayoutManager(mLinearLayoutManager);
        recycle.setHasFixedSize(true);
        recycle.addItemDecoration(new GridItemDividerDecoration(getContext(), R.dimen.divider_height, R.color.divider));
        // TODO: 16/8/13 add  animation
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(zhihuAdapter);
        recycle.addOnScrollListener(loadingMoreListener);
//        recycle.addOnScrollListener(tooldimissListener);

        if (connected) {
            loadDate();
        }


    }

    private void loadDate() {
        if (zhihuAdapter.getItemCount() > 0) {
            zhihuAdapter.clearData();
        }
        currentLoadDate = "0";
        zhihuPresenter.getLastZhihuNews();

    }

    private void loadMoreDate() {
        zhihuAdapter.loadingStart();
        zhihuPresenter.getTheDaily(currentLoadDate);
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

//
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

//
//        tooldimissListener = new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (mLinearLayoutManager.findFirstVisibleItemPosition() < 2) {
//                    if (dy > 0) {
//                        if (toolbarArlp > 0) {
//                            toolbarArlp -= dy;
//                        } else {
//                            toolbarArlp = 0;
//                        }
//                    }
//                    if (dy < 0) {
//                        if (toolbarArlp < 100) {
//                            toolbarArlp -= dy;
//                        } else {
//                            toolbarArlp = 100;
//                        }
//                    }
//
//                    toolbar.setAlpha(toolbarArlp / 100);
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && toolbar.getElevation() != -1) {
//                    toolbar.setElevation(-1f);
//
//                } else if (newState == RecyclerView.SCROLL_STATE_IDLE
//                        && mLinearLayoutManager.findFirstVisibleItemPosition() == 0
//                        && toolbar.getElevation() != 0) {
//                    toolbar.setElevation(1f);
////                    animateToolbar();
////                    zhihuPresenter.getLastZhihuNews();
//                }
//            }
//        };


    }


    @Override
    public void updateList(ZhihuDaily zhihuDaily) {
        if (loading) {
            loading = false;
            zhihuAdapter.loadingfinish();
        }
        currentLoadDate = zhihuDaily.getDate();
        zhihuAdapter.addItems(zhihuDaily.getStories());
//        if the new data is not full of the screen, need load more data
        if (!recycle.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
            loadMoreDate();
        }
    }

    @Override
    public void showProgressDialog() {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if (progress != null) {
            progress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {

        if (recycle != null) {
            Snackbar.make(recycle, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLoadDate.equals("0")) {
                        zhihuPresenter.getLastZhihuNews();
                    } else {
                        zhihuPresenter.getTheDaily(currentLoadDate);
                    }
                }
            }).show();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    private void checkConnectivity(View view) {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected) {
            progress.setVisibility(View.INVISIBLE);
            if (noConnection == null) {
                final ViewStub stub = (ViewStub) view.findViewById(R.id.stub_no_connection);
                noConnection = (ImageView) stub.inflate();
            }
            if (noConnectionText == null) {

                ViewStub stub_text = (ViewStub) view.findViewById(R.id.stub_no_connection_text);
                noConnectionText = (TextView) stub_text.inflate();
            }

            final AnimatedVectorDrawable avd =
                    (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.avd_no_connection);
            noConnection.setImageDrawable(avd);
            avd.start();
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    connectivityCallback);

            monitoringConnectivity = true;
        }

    }



    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connected = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noConnection.setVisibility(View.GONE);
                    noConnectionText.setVisibility(View.GONE);
                    loadDate();
                }
            });
        }

        @Override
        public void onLost(Network network) {
            connected = false;
        }
    };

}
