package com.hongfei02chen.wxautoatreply;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hongfei02chen.wxautoatreply.utils.AppUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mTvGet, mTvSend;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    protected void initView() {
        mTvGet = findViewById(R.id.tv_get);
        mTvSend = findViewById(R.id.tv_send);
        button = findViewById(R.id.btn);
        button.setOnClickListener(this);
    }

    protected void initData() {
        String getService = getPackageName() + "/" + GetFromListService.class.getCanonicalName();
        String sendService = getPackageName() + "/" + SendService.class.getCanonicalName();
        boolean isGetServiceOn = AppUtils.isAccessibilitySettingOn(this, getService);
        boolean isSendServiceOn = AppUtils.isAccessibilitySettingOn(this, sendService);
        if (isGetServiceOn) {
            mTvGet.setText("get service is on");
        } else {
            mTvGet.setText("get service is off");
        }
        if (isSendServiceOn) {
            mTvSend.setText("send service is on");
        } else {
            mTvSend.setText("send service is off");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
