package com.hongfei02chen.xpwechathelper.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * created by chenhongfei on 2018/6/14
 */
public class KeepAliveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        // 把这个一个像素点设置在左上角。
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        // 设置一个像素
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);
    }
}
