package com.looklook.xinghongfei.looklook;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.looklook.xinghongfei.looklook.Acivity.BaseActivity;
import com.looklook.xinghongfei.looklook.adapter.ZhihuAdapter;
import com.looklook.xinghongfei.looklook.bean.zhihu.ZhihuDaily;
import com.looklook.xinghongfei.looklook.presenter.implPresenter.ZhihuPresenterImpl;
import com.looklook.xinghongfei.looklook.presenter.implView.IZhihuFragment;
import com.looklook.xinghongfei.looklook.util.AnimUtils;
import com.looklook.xinghongfei.looklook.util.ViewUtils;
import com.looklook.xinghongfei.looklook.view.GridItemDividerDecoration;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements IZhihuFragment {

    ImageView noConnection;
    TextView noConnectionText;
    MenuItem currountMenuitem;
    boolean connected = true;
    boolean monitoringConnectivity;
    boolean loading;
    ZhihuAdapter zhihuAdapter;


    //    boolean isLoadFromCache;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;
    @InjectView(R.id.grid)
    RecyclerView grid;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.nav_view)
    NavigationView navView;
    @InjectView(R.id.drawer)
    DrawerLayout drawer;
    @InjectView(R.id.status_bar_background)
    View statusBarBackground;
    @InjectView(R.id.prograss)
    ProgressBar progress;
    ZhihuPresenterImpl zhihuPresenter;
    private String currentLoadDate;
    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connected = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(drawer);
                    noConnection.setVisibility(View.GONE);
//                    progress.setVisibility(View.VISIBLE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.inject(this);

        zhihuPresenter = new ZhihuPresenterImpl(this);

        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);


        zhihuAdapter = new ZhihuAdapter(this);
        setActionBar(toolbar);

        initialNevig();

        initialListener();

        initialgrid();

        //// TODO: 16/8/15 ???
        checkConnectivity();

        initialDate();

        // drawer layout treats fitsSystemWindows specially so we have to handle insets ourselves
        drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(lpToolbar);

                // inset the grid top by statusbar+toolbar & the bottom by the navbar (don't clip)
                grid.setPadding(grid.getPaddingLeft(),
                        insets.getSystemWindowInsetTop() + ViewUtils.getActionBarSize
                                (MainActivity.this),
                        grid.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                        grid.getPaddingBottom() + insets.getSystemWindowInsetBottom());


                View postingStub = findViewById(R.id.stub_posting_progress);
                ViewGroup.MarginLayoutParams lpPosting =
                        (ViewGroup.MarginLayoutParams) postingStub.getLayoutParams();
                lpPosting.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpPosting.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                postingStub.setLayoutParams(lpPosting);

                // we place a background behind the status bar to combine with it's semi-transparent
                // color to get the desired appearance.  Set it's height to the status bar height
                View statusBarBackground = findViewById(R.id.status_bar_background);
                FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                        statusBarBackground.getLayoutParams();
                lpStatus.height = insets.getSystemWindowInsetTop();
                statusBarBackground.setLayoutParams(lpStatus);

                // inset the filters list for the status bar / navbar
                // need to set the padding end for landscape case

                // clear this listener so insets aren't re-applied
                drawer.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });

        if (savedInstanceState == null) {
            animateToolbar();
            if (currountMenuitem == null) {

                currountMenuitem = navView.getMenu().findItem(R.id.zhihuitem);
                if (currountMenuitem != null) {
                    currountMenuitem.setChecked(true);

                }


            }
        }

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
                        // TODO: 16/8/13  loading more date
                        loadMoreDate();
                    }
                }
            }
        };

    }

    private void initialDate() {


        if (connected) {
            loadDate();
        }

//        if (SharePreferenceUtil.isRefreshOnlyWifi(this)) {
//            if (NetWorkUtil.isWifiConnected(this)) {
//                loadDate();
//            } else {
//                isLoadFromCache=true;
//                zhihuPresenter.getLastFromCache();
//            }
//        } else {
//            if (NetWorkUtil.isNetWorkAvailable(this)) {
//                loadDate();
//            } else {
//                isLoadFromCache=true;
//                zhihuPresenter.getLastFromCache();
//            }
//
//        }

    }

    private void loadDate() {
        if (zhihuAdapter.getItemCount() > 0) {
            zhihuAdapter.clearData();
        }
        currentLoadDate = "0";
        zhihuPresenter.getLastZhihuNews();
        Log.d("maat", "loaddata");

    }


    private void initialgrid() {
        mLinearLayoutManager=new LinearLayoutManager(MainActivity.this);
        grid.setLayoutManager(mLinearLayoutManager);
        grid.setHasFixedSize(true);
        grid.addItemDecoration(new GridItemDividerDecoration(this, R.dimen.divider_height, R.color.divider));
        // TODO: 16/8/13 add  animation
        grid.setItemAnimator(new DefaultItemAnimator());
        grid.setAdapter(zhihuAdapter);
        grid.addOnScrollListener(loadingMoreListener);
        Log.d("maat", "initialgrid");
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zhihuPresenter.unsubcrible();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected) {
            progress.setVisibility(View.INVISIBLE);
            if (noConnection == null) {
                final ViewStub stub = (ViewStub) findViewById(R.id.stub_no_connection);
                noConnection = (ImageView) stub.inflate();
            }
            if (noConnectionText == null) {

                ViewStub stub_text = (ViewStub) findViewById(R.id.stub_no_connection_text);
                noConnectionText = (TextView) stub_text.inflate();
            }

            final AnimatedVectorDrawable avd =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.avd_no_connection);
            noConnection.setImageDrawable(avd);
            avd.start();

            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    connectivityCallback);
            monitoringConnectivity = true;
        }
    }

    private void initialNevig() {

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (currountMenuitem != item) {

                    currountMenuitem.setChecked(false);
                    currountMenuitem = item;
                    item.setChecked(true);
                    drawer.closeDrawer(GravityCompat.END, true);
                }
                return true;
            }
        });
    }

    private void loadMoreDate() {
        zhihuAdapter.loadingStart();
        zhihuPresenter.getTheDaily(currentLoadDate);
    }

    @Override
    public void updateList(ZhihuDaily zhihuDaily) {

        Log.d("maat", "updatelist"+zhihuDaily.getStories().size() + "");

        if (loading) {
            loading = false;
            zhihuAdapter.loadingfinish();
        }
        currentLoadDate = zhihuDaily.getDate();
        zhihuAdapter.addItems(zhihuDaily.getStories());
//        if the new data is not full of the screen, need load more data
//        if (!grid.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)) {
//            loadMoreDate();
//        }
    }

    @Override
    public void showProgressDialog() {
//        load the last date
//        first come to here  progress dialog  is showwing
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
        Log.d("maat", "main_erro");

        if (grid != null) {
            Snackbar.make(grid, getString(R.string.snack_infor), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
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


    private void animateToolbar() {
        // this is gross but toolbar doesn't expose it's children to animate them :(
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;

            // fade in and space out the title.  Animating the letterSpacing performs horribly so
            // fake it by setting the desired letterSpacing then animating the scaleX ¯\_(ツ)_/¯
            title.setAlpha(0f);
            title.setScaleX(0.8f);

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(500)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this)).start();
        }
        View amv = toolbar.getChildAt(1);
        if (amv != null & amv instanceof ActionMenuView) {
            ActionMenuView actions = (ActionMenuView) amv;
            popAnim(actions.getChildAt(0), 500, 200); // filter
            popAnim(actions.getChildAt(1), 700, 200); // overflow
        }
    }

    private void popAnim(View v, int startDelay, int duration) {
        if (v != null) {
            v.setAlpha(0f);
            v.setScaleX(0f);
            v.setScaleY(0f);

            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .setInterpolator(AnimationUtils.loadInterpolator(this,
                            android.R.interpolator.overshoot)).start();
        }
    }

    //    when recycle view scroll bottom,need loading more date and show the more view.
    public interface LoadingMore {

        void loadingStart();

        void loadingfinish();
    }


}



