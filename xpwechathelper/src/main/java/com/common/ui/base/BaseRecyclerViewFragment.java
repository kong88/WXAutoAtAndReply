package com.common.ui.base;

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

import java.util.ArrayList;
import java.util.List;

/**
 * created by chenhongfei on 2018/6/7
 */
public abstract class BaseRecyclerViewFragment<D> extends Fragment {
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected List<D> mDataList = new ArrayList<>();

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
        mAdapter = setAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        setDecoration();
    }

    protected void setLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    protected abstract RecyclerView.Adapter setAdapter(List<D> dataList);

    protected void setDecoration() {

    }

    protected abstract void loadData();
}
