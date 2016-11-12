package com.example.ruolan.computernews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.ruolan.computernews.R;
import com.example.ruolan.computernews.widget.TranslucentUtils;

/**
 * Created by Administrator on 2016/10/20.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getResultId());

        TranslucentUtils.setColor(this, getResources().getColor(R.color.home_bg_title), 1);

        initView();
        initListener();
        initData();
    }


    protected abstract int getResultId();

    public void initData() {

    }

    protected abstract void initListener();

    public abstract void initView();


}
