package com.hongfei02chen.xpwechathelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hongfei02chen.xpwechathelper.R;
import com.hongfei02chen.xpwechathelper.bean.ChatRoomBean;
import com.hongfei02chen.xpwechathelper.bean.DbChatRoomHelper;
import com.hongfei02chen.xpwechathelper.ui.adapter.RAdapterGroupInfoItem;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * created by chenhongfei on 2018/6/7
 */
public class GroupFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<ChatRoomBean> mDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, null);
        initView(rootView);
        loadData();
        return rootView;
    }

    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        setLayoutManager();
        setAdapter();
        setDecoration();
        getActivity().setTitle(R.string.app_name);
    }

    protected void setLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    protected void setAdapter() {
        mAdapter = new RAdapterGroupInfoItem(getActivity(), mDataList);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void setDecoration() {

    }

    protected void loadData() {
        mDataList.clear();
        List<ChatRoomBean> list = DbChatRoomHelper.queryList();
        if (!CollectionUtils.isEmpty(list)) {
            mDataList.addAll(list);
        }
        mAdapter.notifyDataSetChanged();
    }
}
