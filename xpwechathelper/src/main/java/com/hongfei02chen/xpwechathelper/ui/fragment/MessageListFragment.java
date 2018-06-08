package com.hongfei02chen.xpwechathelper.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.common.ui.base.BaseRecyclerViewFragment;
import com.hongfei02chen.xpwechathelper.bean.DbJoinMessageBeanHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;
import com.hongfei02chen.xpwechathelper.ui.adapter.RAdapterMessageInfoItem;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/8
 */
public class MessageListFragment extends BaseRecyclerViewFragment<JoinMessageBean> {


    @Override
    protected RecyclerView.Adapter setAdapter(List<JoinMessageBean> dataList) {
        return new RAdapterMessageInfoItem(getActivity(), dataList);
    }

    @Override
    protected void loadData() {
        mDataList.clear();
        List<JoinMessageBean> list = DbJoinMessageBeanHelper.queryList();
        if (!CollectionUtils.isEmpty(list)) {
            mDataList.addAll(list);
        }
        mAdapter.notifyDataSetChanged();
    }
}
