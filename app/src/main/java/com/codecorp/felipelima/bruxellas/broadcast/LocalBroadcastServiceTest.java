package com.codecorp.felipelima.bruxellas.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codecorp.felipelima.bruxellas.services.ServiceTest;

public class LocalBroadcastServiceTest extends BroadcastReceiver {

    private ServiceTest service;

    public LocalBroadcastServiceTest(ServiceTest service){
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String mensagem = intent.getStringExtra(ServiceTest.MENSAGEM_KEY);
        service.logMensagem(mensagem);
    }
}
