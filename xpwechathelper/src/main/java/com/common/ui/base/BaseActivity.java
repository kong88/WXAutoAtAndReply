package com.common.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * created by chenhongfei on 2018/6/7
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        initParams(savedInstanceState);
        initView();
        loadData();
    }
    protected abstract int getContentViewId();

    protected abstract void initParams(Bundle savedInstanceState);

    protected abstract void initView();

    protected abstract void loadData();
}
