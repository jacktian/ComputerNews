package com.example.ruolan.computernews.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ruolan.computernews.constant.Contants;
import com.example.ruolan.computernews.utils.NetWorkUtils;
import com.example.ruolan.computernews.widget.FlyBanner;
import com.example.ruolan.computernews.constant.HttpUrlPaths;
import com.example.ruolan.computernews.activity.NewsDetailActivity;
import com.example.ruolan.computernews.R;
import com.example.ruolan.computernews.adapter.NewsAdapter;
import com.example.ruolan.computernews.bean.NewsBannerBean;
import com.example.ruolan.computernews.bean.NewsBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import rx.internal.schedulers.NewThreadWorker;

/**
 * Created by Administrator on 2016/11/10.
 * 新闻fragment
 */

public class ComputerNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    //新闻类型
    private String mType;
    //新闻url
    private String newsUrl;
    //轮播图
    private String bannerUrl;

    private String title;

    private int startIndex = 0;
    private int endIndex = 19;

    //上拉加载更多的时候请求数量
    private int count = 20;
    //总共页数
    private int totalPage;
    //当前页数
    private int currentPage = 1;
    private List<NewsBean.DatasEntity> mDatasEntities = new ArrayList<>();

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private FlyBanner mFlyBanner;


    private RelativeLayout mErrorView;
    private Button mTryAgain;

    boolean isHasData = false;


    public static ComputerNewsFragment newInstance(String index, String title) {
        // Required empty public constructor
        Bundle bundle = new Bundle();
        bundle.putString(Contants.INDEX, index);
        bundle.putString(Contants.TITLE, title);
        ComputerNewsFragment fragment = new ComputerNewsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_computer_news_layout, container, false);
        mType = getArguments().getString(Contants.INDEX);
        title = getArguments().getString(Contants.TITLE);
        bannerUrl = HttpUrlPaths.getNewBanner(mType);
        newsUrl = HttpUrlPaths.getNewsData(mType, startIndex, endIndex);
        initView(view);

        if (NetWorkUtils.isOnline(getContext())) {

            initData();

            if (!isHasData) {
                //isHasData = false;
                new Handler().postDelayed(() -> {
                    if (!isHasData) {  //如果5秒之后还没有请求到数据
                        isHasData = false;
                        errorHide();
                        OkGo.getInstance().cancelTag(this);  //取消此次请求
                    } else {

                    }
                }, 5000);
            }
        } else {
            errorShow();
        }
        return view;
    }

    /**
     * 隐藏error
     */
    private void errorHide() {
        mErrorView.setVisibility(View.VISIBLE);
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setVisibility(View.GONE);
    }

    /**
     * 显示error
     */
    private void errorShow() {
        mRefreshLayout.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void initData() {

        OkGo.get(newsUrl)
                .tag(this)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.d("ComputerNewsFragment", s);
                        Type type = new TypeToken<NewsBean>() {
                        }.getType();
                        NewsBean bean = new Gson().fromJson(s, type);
                        if (bean.getCount() > 0) {
                            isHasData = true;
                            mErrorView.setVisibility(View.GONE);
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            mRefreshLayout.setRefreshing(false);  //加载完事消失加载条
                            mDatasEntities = bean.getDatas();
                            totalPage = bean.getEndIndex() / 20;
                            mNewsAdapter = new NewsAdapter(getContext(), mDatasEntities);

                            mNewsAdapter.setOnItemClickListener((view, position, categoryBean) -> {
                                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                                intent.putExtra("id", categoryBean.getId());
                                intent.putExtra(Contants.TITLE, title);
                                startActivity(intent);
                            });

                            mRecyclerView.setAdapter(mNewsAdapter);
                            mNewsAdapter.setEntities(mDatasEntities);
                            setHeaderView();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("ComputerNewsFragment", e.toString());
                    }
                });

//        OkGo.get(newsUrl)
//                .getCall(StringConvert.create(), RxAdapter.<String>create())
//                .doOnSubscribe(() -> {
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> {
//                    Type type = new TypeToken<NewsBean>() {
//                    }.getType();
//                    NewsBean bean = new Gson().fromJson(s, type);
//                    if (bean.getCount() > 0) {
//                        mDatasEntities = bean.getDatas();
//                        totalPage = bean.getEndIndex() / 20;
//                        mNewsAdapter.setEntities(mDatasEntities);
//                    }
//                }, throwable -> {
//                    Log.d("ComputerNewsFragment", throwable.toString());
//                });
    }

    private void setHeaderView() {
        View headerView = LayoutInflater.from(getContext())
                .inflate(R.layout.news_banner_item_layout, null);
        mFlyBanner = (FlyBanner) headerView.findViewById(R.id.fly_banner);
        OkGo.get(bannerUrl)
                .tag(this)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Type type = new TypeToken<NewsBannerBean>() {
                        }.getType();
                        NewsBannerBean bannerBean = new Gson().fromJson(s, type);
                        if (bannerBean.getCount() > 0) {
                            List<String> imgBanner = new ArrayList<>();
                            for (NewsBannerBean.DatasEntity entity : bannerBean.getDatas()) {
                                imgBanner.add(entity.getHeadPicUrl());
                            }
                            mFlyBanner.setImagesUrl(imgBanner);
                            mFlyBanner.setOnItemClickListener(position -> {
                                String id = bannerBean.getDatas().get(position).getId();
                                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra(Contants.TITLE, title);
                                startActivity(intent);
                            });
                        }
                    }
                });
        mNewsAdapter.setHeaderView(headerView);

    }

    private void initView(View view) {
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mRefreshLayout.setColorSchemeColors(Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN);
        //能够模拟进入就刷新
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(true));
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisiableItemPosition = manager.findLastVisibleItemPosition();
                if (lastVisiableItemPosition + 1 == mNewsAdapter.getItemCount()) {
                    if (!isLoading) {
                        isLoading = true;
                        new Handler().postDelayed(() -> {
                            getMoreData();
                            isLoading = false;
                            mNewsAdapter.notifyItemRemoved(mNewsAdapter.getItemCount());
                        }, 3000);
                    }
                }
            }
        });
        //mNewsAdapter = new NewsAdapter(getContext(),mDatasEntities);
        //  mRecyclerView.setAdapter(mNewsAdapter);

        mErrorView = (RelativeLayout) view.findViewById(R.id.error_view);
        mErrorView.setVisibility(View.GONE);
        mTryAgain = (Button) view.findViewById(R.id.errorStateButton);
        mTryAgain.setOnClickListener(this);
    }

    /**
     * 获取更多数据  分页加载
     */
    private void getMoreData() {
        currentPage++;
        startIndex += count;
        endIndex += count;
        if (currentPage > totalPage + 1) {
            mNewsAdapter.notifyItemRemoved(mNewsAdapter.getItemCount());
            Toast.makeText(getContext(), getActivity().getResources()
                    .getString(R.string.loading_finish), Toast.LENGTH_SHORT).show();
            return;
        }
        newsUrl = HttpUrlPaths.getNewsData(mType, startIndex, endIndex);
        OkGo.get(newsUrl)
                .tag(this)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.d("ComputerNewsFragment", s);
                        Type type = new TypeToken<NewsBean>() {
                        }.getType();
                        NewsBean bean = new Gson().fromJson(s, type);
                        if (bean.getCount() > 0) {
                            mRefreshLayout.setRefreshing(false);  //加载完事消失加载条
                            List<NewsBean.DatasEntity> datas = bean.getDatas();
                            mDatasEntities.addAll(datas);
                            // totalPage = bean.getEndIndex() / 20;
                            // mNewsAdapter = new NewsAdapter(getContext(), mDatasEntities);
                            // mRecyclerView.setAdapter(mNewsAdapter);
                            mNewsAdapter.setEntities(mDatasEntities);
                            // setHeaderView();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("ComputerNewsFragment", e.toString());
                    }
                });
    }

    private boolean isLoading;

    @Override
    public void onRefresh() {

        isHasData = false;
        refreshData();
        showErrorView();
    }

    /**
     * 判断是否有网，和网络较慢导致数据请求失败的提示
     */
    private void showErrorView() {
        mErrorView.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        if (!isHasData && !NetWorkUtils.isOnline(getContext())) {
            isHasData = false;
            new Handler().postDelayed(() -> {
                errorHide();
                OkGo.getInstance().cancelTag(this);  //取消此次请求
            }, 5000);
        }
    }

    private void refreshData() {
        mDatasEntities.clear();
        currentPage = 1;
        startIndex = 0;
        endIndex = 19;
        if (currentPage > totalPage + 1) {
            mNewsAdapter.notifyItemRemoved(mNewsAdapter.getItemCount());
            Toast.makeText(getContext(), getActivity().getResources()
                    .getString(R.string.loading_finish), Toast.LENGTH_SHORT).show();
            return;
        }
        newsUrl = HttpUrlPaths.getNewsData(mType, startIndex, endIndex);
        OkGo.get(newsUrl)
                .tag(this)
                .cacheMode(CacheMode.DEFAULT)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.d("ComputerNewsFragment", s);
                        Type type = new TypeToken<NewsBean>() {
                        }.getType();
                        NewsBean bean = new Gson().fromJson(s, type);
                        if (bean.getCount() > 0) {
                            mRefreshLayout.setRefreshing(false);  //加载完事消失加载条
                            List<NewsBean.DatasEntity> datas = bean.getDatas();
                            mDatasEntities.addAll(datas);
                            // totalPage = bean.getEndIndex() / 20;
                            // mNewsAdapter = new NewsAdapter(getContext(), mDatasEntities);
                            // mRecyclerView.setAdapter(mNewsAdapter);
                            mNewsAdapter.setEntities(mDatasEntities);
                            // setHeaderView();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("ComputerNewsFragment", e.toString());
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.errorStateButton){
            isHasData = false;
            initData();
            showErrorView();
        }
    }
}
