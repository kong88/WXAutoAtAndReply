package com.common.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TabHost;

import com.hongfei02chen.xpwechathelper.R;

/**
 * created by chenhongfei on 2018/6/7
 */
public abstract class BaseMultiFragmentActivity extends BaseActivity {
    private static final String TAG = "BaseMultiFragmentActivity";
    protected FragmentTabHost mTabHost;
    protected Class mFragmentArray[];
    // Tab选项卡的文字
    protected String mTextArray[];
    // Tab选项卡的icon
    protected int mIconArray[];
    protected String mTitle;


    @Override
    protected void initParams(Bundle savedInstanceState) {
        mFragmentArray = getFragmentArray();
        mTextArray = getTextArray();
        mIconArray = getIconArray();

        mTitle = getIntent().getStringExtra("title");
    }

    @Override
    protected void initView() {
        mTabHost = getFragmentTabHost();
        mTabHost.setup(this, getSupportFragmentManager(), getContentId());
        mTabHost.getTabWidget().setDividerDrawable(getDividerDrawableColorId());
        for (int i = 0; i < mFragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragmentArray[i], getBundle(i));
        }
        mTabHost.getTabWidget().setStripEnabled(false);
    }

    protected abstract FragmentTabHost getFragmentTabHost();

    protected abstract Class[] getFragmentArray();

    protected abstract String[] getTextArray();

    protected abstract int[] getIconArray();

    protected abstract Bundle getBundle(int index);

    protected abstract int getContentId();
    /**
     * 给Tab按钮设置图标和文字
     */
    protected abstract View getTabItemView(int index);

    protected int getDividerDrawableColorId() {
        return R.color.transparent;
    }
}