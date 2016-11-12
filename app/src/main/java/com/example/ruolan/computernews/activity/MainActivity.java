package com.example.ruolan.computernews.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.example.ruolan.computernews.R;
import com.example.ruolan.computernews.adapter.ViewPagerAdapter;
import com.example.ruolan.computernews.base.BaseActivity;
import com.example.ruolan.computernews.fragment.ComputerNewsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String[] type = new String[]{"higo","player","indoorsfun","look"};

    private List<String> mTitles;
    private List<Fragment> mFragments;
    private ViewPagerAdapter mAdapter;


    @Override
    protected int getResultId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {

    }

    /**
     * 初始化标题
     */
    private void initTitles() {
        mTitles = new ArrayList<>();
        mTitles.add(getResources().getString(R.string.higo));
        mTitles.add(getResources().getString(R.string.player));
        mTitles.add(getResources().getString(R.string.indoorsfun));
        mTitles.add(getResources().getString(R.string.look));
    }


    @Override
    public void initView() {
        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
        this.mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        initTitles();
        initFragments();
        initTabs();
    }

    private void initTabs() {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        for (int i = 0; i < type.length; i++) {
            mFragments.add(ComputerNewsFragment.newInstance(type[i],mTitles.get(i)));
        }

    }
}
