package com.hongfei02chen.xpwechathelper;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.hongfei02chen.xpwechathelper.ui.activity.KeepAliveActivity;
import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.ViewUtils;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TransferDataService extends JobService {
    public TransferDataService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "onStartJob", Toast.LENGTH_SHORT).show();
        Message m = Message.obtain();
        m.obj = params;
        mHanlder.sendMessage(m);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHanlder.removeCallbacksAndMessages(null);
        return true;
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            JobParameters params = (JobParameters) msg.obj;
            jobFinished(params, true);
            Intent intent = new Intent(getApplicationContext(), KeepAliveActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startWechat();
                }
            }, 5000);
        }
    };

    protected void startWechat() {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(ViewUtils.MM_PNAME, "com.tencent.mm.ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        startActivity(intent);
    }

}
