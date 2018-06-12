package com.hongfei02chen.xpwechathelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.common.ui.base.BaseRecyclerViewFragment;
import com.hongfei02chen.xpwechathelper.R;
import com.hongfei02chen.xpwechathelper.bean.DbJoinMessageBeanHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;
import com.hongfei02chen.xpwechathelper.ui.adapter.RAdapterMessageInfoItem;
import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/8
 */
public class MessageListFragment extends BaseRecyclerViewFragment<JoinMessageBean> {

    private int mState = 0;
    private String mTitle;

    @Override
    protected void initParams(Bundle savedInstanceState) {
        super.initParams(savedInstanceState);
        mTitle = "状态:待发送";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        setViewInfo();
    }

    @Override
    protected RecyclerView.Adapter setAdapter(List<JoinMessageBean> dataList) {
        return new RAdapterMessageInfoItem(getActivity(), dataList);
    }

    @Override
    protected void loadData() {
        mDataList.clear();
        List<JoinMessageBean> list = DbJoinMessageBeanHelper.queryList(mState);
        if (!CollectionUtils.isEmpty(list)) {
            mDataList.addAll(list);
        }
        mAdapter.notifyDataSetChanged();
    }

    protected void setViewInfo() {
        getActivity().setTitle(mTitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        AppLog.debug("fragment", "onCreateOptionsMenu===");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AppLog.debug("fragment", "onCreateContextMenu===");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        AppLog.debug("fragment", "onOptionsItemSelected===:" + item.getGroupId() + "," + item.getItemId() + "," + item.getMenuInfo() + " ," + item.getTitle());
        switch (item.getItemId()) {
            case R.id.state_all:
                mState = -1;
                break;
            case R.id.state_0:
                mState = 0;
                break;
            case R.id.state_1:
                mState = 1;
                break;
        }
        mTitle = item.getTitle().toString();
        setViewInfo();
        loadData();
        return true;
    }
}
