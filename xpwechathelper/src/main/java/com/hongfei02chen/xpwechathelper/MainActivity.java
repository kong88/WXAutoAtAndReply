package com.hongfei02chen.xpwechathelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = findViewById(R.id.tv_text);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showModuleActiveInfo(false);
    }

    /**
     * 模块激活信息
     *
     * @param isModuleActive
     */
    private void showModuleActiveInfo(boolean isModuleActive) {
        if (!isModuleActive) {
            Toast.makeText(this, "模块未激活", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "模块已激活", Toast.LENGTH_SHORT).show();
        }
    }
}
