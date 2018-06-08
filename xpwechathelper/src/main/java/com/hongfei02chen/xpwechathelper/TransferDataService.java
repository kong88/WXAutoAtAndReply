package com.hongfei02chen.xpwechathelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TransferDataService extends Service {
    public TransferDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
