package com.example.ruolan.computernews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();
    private  List<String> mFragmentTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> strings) {
        super(fm);
        this.mFragments = fragments;
        this.mFragmentTitles = strings;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    /**
     *
     * @param fragment   添加的fragment
     * @param fragmentTitle   fragment的标题
     */
    public void addFragment(Fragment fragment, String fragmentTitle){
        mFragments.add(fragment);
        mFragmentTitles.add(fragmentTitle);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}
