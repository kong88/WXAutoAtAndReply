package com.hongfei02chen.xpwechathelper.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hongfei02chen.xpwechathelper.R;
import com.hongfei02chen.xpwechathelper.SendService;
import com.hongfei02chen.xpwechathelper.eventbus.MessageEvent;
import com.hongfei02chen.xpwechathelper.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * created by chenhongfei on 2018/6/7
 */
public class StatusFragment extends Fragment implements View.OnClickListener {
    TextView mTvHook, mTvSend;
    Button button;

    private boolean mIsHook = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_status, null);
        initView(rootView);

        return rootView;
    }

    protected void initView(View view) {
        mTvHook = view.findViewById(R.id.tv_hook);
        mTvSend = view.findViewById(R.id.tv_send_service);
        button = view.findViewById(R.id.btn);
        button.setOnClickListener(this);

        getActivity().setTitle(R.string.app_name);
        EventBus.getDefault().register(this);
    }

    protected void initData() {

        String sendService = getActivity().getPackageName() + "/" + SendService.class.getCanonicalName();

        boolean isSendServiceOn = AppUtils.isAccessibilitySettingOn(getActivity(), sendService);
        if (mIsHook) {
            mTvHook.setText("xpose激活模块");
        } else {
            mTvHook.setText("xpose没有激活模块");
        }
        if (isSendServiceOn) {
            mTvSend.setText("send service is on");
        } else {
            mTvSend.setText("send service is off");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent messageEvent) {
        Log.d("xpose", "receive it");
        mIsHook = messageEvent.isHook();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
