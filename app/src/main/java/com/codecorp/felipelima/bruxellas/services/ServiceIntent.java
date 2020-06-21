package com.codecorp.felipelima.bruxellas.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class ServiceIntent extends IntentService{

    private int count;
    private boolean ativo;
    private boolean stopAll;

    public ServiceIntent() {
        super("ServiceIntentThread");

        count = 0;
        ativo = true;
        stopAll = true;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        Bundle b = intent.getExtras();
        if (b != null){
            int desligar = b.getInt("desligar");
            if (desligar == 1){
                stopAll = false;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while(stopAll && ativo && count < 5){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            count++;
            Log.i("Script","Count: "+count);
        }

        ativo = true;
        count = 0;
    }
}
