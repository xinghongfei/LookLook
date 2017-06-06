package com.looklook.xinghongfei.looklook.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.looklook.xinghongfei.looklook.R;
import com.looklook.xinghongfei.looklook.adapter.ZhihuAdapter;
import com.looklook.xinghongfei.looklook.bean.zhihu.ZhihuDaily;
import com.looklook.xinghongfei.looklook.presenter.implPresenter.ZhihuPresenterImpl;
import com.looklook.xinghongfei.looklook.presenter.implView.IZhihuFragment;
import com.looklook.xinghongfei.looklook.view.GridItemDividerDecoration;
import com.looklook.xinghongfei.looklook.widget.WrapContentLinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinghongfei on 16/8/17.
 */
public class ZhihuFragment extends BaseFragment implements IZhihuFragment {

    TextView noConnectionText;
    boolean loading;
    ZhihuAdapter zhihuAdapter;
    boolean connected = true;
    boolean monitoringConnectivity;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;

    View view = null;
    ZhihuPresenterImpl zhihuPresenter;
    @BindView(R.id.recyclerview_zhihu)
    RecyclerView recyclerView_zhihu;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private String currentLoadDate;
    private ConnectivityManager.NetworkCallback connectivityCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setRetainInstance(true);
        view = inflater.inflate(R.layout.zhihu_fragment_layout, container, false);
        checkConnectivity(view);
        ButterKnife.bind(this, view);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.unregisterNetworkCallback(connectivityCallback);
            }
            monitoringConnectivity = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zhihuPresenter.unsubscrible();

    }

    private void initialDate() {
        zhihuPresenter = new ZhihuPresenterImpl(getContext(), this);
        zhihuAdapter = new ZhihuAdapter(getContext());
    }

    private void initialView() {

        initialListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLinearLayoutManager = new WrapContentLinearLayoutManager(getContext());

        } else {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
        }
        recyclerView_zhihu.setLayoutManager(mLinearLayoutManager);
        recyclerView_zhihu.setHasFixedSize(true);
        recyclerView_zhihu.addItemDecoration(new GridItemDividerDecoration(getContext(), R.dimen.divider_height, R.color.divider));
        // TODO: 16/8/13 add  animation
        recyclerView_zhihu.setItemAnimator(new DefaultItemAnimator());
        recyclerView_zhihu.setAdapter(zhihuAdapter);
        recyclerView_zhihu.addOnScrollListener(loadingMoreListener);
//      recyclerView_zhihu.addOnScrollListener(tooldimissListener);
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

                if(dy < 0) //TODO 向上滚动, 即下拉。下拉的判断条件应该更严格 考虑增加下拉刷新功能
                {
                    Log.i("ZhihuFragment", "下拉刷新");
                    Toast.makeText(getActivity(), "下拉刷新", Toast.LENGTH_SHORT);
                }
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    connected = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
        if (!recyclerView_zhihu.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
            loadMoreDate();
        }
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {

        if (recyclerView_zhihu != null) {
            Snackbar.make(recyclerView_zhihu, getString(R.string.snack_infor), Snackbar.LENGTH_LONG).setAction("重试", new View.OnClickListener() {
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
//        ButterKnife.reset(this);
    }

    private void checkConnectivity(View view) {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected && progressBar != null) {//不判断容易抛出空指针异常
            progressBar.setVisibility(View.INVISIBLE);
            if (noConnectionText == null) {

                ViewStub stub_text = (ViewStub) view.findViewById(R.id.stub_no_connection_text);
                noConnectionText = (TextView) stub_text.inflate();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                        connectivityCallback);

                monitoringConnectivity = true;
            }

        }

    }


}
