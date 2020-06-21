package com.codecorp.felipelima.bruxellas.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.services.ServiceTest;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

public class BroadcastReceiverTest2 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Script","BroadcastReceiverBootCompleted");

        Intent it = new Intent(context, ServiceTest.class);
        context.startService(it);
    }
}
