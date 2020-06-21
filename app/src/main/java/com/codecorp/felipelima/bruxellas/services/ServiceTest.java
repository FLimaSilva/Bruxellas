package com.codecorp.felipelima.bruxellas.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;
import android.util.EventLog;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.broadcast.LocalBroadcastServiceTest;
import com.codecorp.felipelima.bruxellas.eventbus.MessageEB;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.view.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ServiceTest extends Service {

    public static final String FILTRO_KEY = "ServiceTest_KEY";
    public static final String MENSAGEM_KEY = "ServiceTest_MENSAGEM_KEY";

    private LocalBroadcastServiceTest broadcast;

    public List<Worker> threads = new ArrayList<Worker>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //EventBus Register
        EventBus.getDefault().register(ServiceTest.this);

        //broadcast = new LocalBroadcastServiceTest(this);
        //IntentFilter intentFilter = new IntentFilter(FILTRO_KEY);
        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcast,intentFilter);

        Log.i("Script","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Script","onStartCommand");

        Worker w = new Worker(startId);
        w.start();
        threads.add(w);

        return START_STICKY;

        //return super.onStartCommand(intent, flags, startId);
        //START_NOT_STICKY Quando o android aniquila o seu serviço ele não reestarta de volta o serviço
        //START_STICKY Quando o android aniquila o seu serviço ele reestarta de volta o serviço
        //START_REDELIVER_INTENT
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast);

        for (int i = 0, tam = threads.size(); i < tam; i++){
            threads.get(i).ativo = false;
        }

        //EventBus Unregister
        EventBus.getDefault().unregister(ServiceTest.this);
    }

    public void logMensagem(String mensagem){
        Log.i("Script","testeBroadcast"+mensagem);
    }

    class Worker extends Thread{
        public int count = 0;
        public int startId;
        public boolean ativo = true;

        public Worker (int startId) {
            this.startId = startId;
        }

        public void run(){
            while (ativo && count <100){

                MessageEB m = new MessageEB();
                m.setClassTester(ServiceTest.class+"");
                m.setNumber(count+1);
                m.setText("Random Message: "+(count+1));

                EventBus.getDefault().post(m);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                count ++;
                Log.i("Script", "COUNT: "+count);

            }
            stopSelf(startId);
        }
    }

    @Subscribe
    public void onEvent(MessageEB mMessageEb){
        if (!mMessageEb.getClassTester().equalsIgnoreCase(ServiceTest.class+""))
            return;

        Pedidos p = new Pedidos();
        p.setQuant_prato("5");
        p.setPrato("Arroz com feijão");

        List<Pedidos> list = new ArrayList<>();
        list.add(p);

        mMessageEb.setClassTester(MainActivity.class+"");
        mMessageEb.setList(list);

        EventBus.getDefault().postSticky(mMessageEb);
    }
}
