package com.codecorp.felipelima.bruxellas.view;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.eventbus.MessageEB;
import com.codecorp.felipelima.bruxellas.fragments.CadastroFragment;
import com.codecorp.felipelima.bruxellas.fragments.CozinhaFragment;
import com.codecorp.felipelima.bruxellas.fragments.EstoqueFragment;
import com.codecorp.felipelima.bruxellas.fragments.PedidosFragment;
import com.codecorp.felipelima.bruxellas.fragments.SobreFragment;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.services.JobSchedulerService;
import com.codecorp.felipelima.bruxellas.services.ServiceTest;
import com.codecorp.felipelima.bruxellas.util.ListarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FrameLayout frameLayout;
    Bundle bd;
    int carregaFrag = 0;
    NavigationView navigationView;

    CozinhaFragment cozinhaFragment;
    EstoqueFragment estoqueFragment;
    PedidosFragment pedidosFragment;
    CadastroFragment cadastroFragment;
    SobreFragment sobreFragment;

    RestauranteController controller;

    Context context;

    public FloatingActionButton fab;
    private WifiManager wifi;

    private String[] itens = new String[]{"Mesa 01","Mesa 02","Mesa 03","Mesa 04",
                                          "Mesa 05","Mesa 06","Mesa 07","Mesa 08",
                                          "Mesa 09","Mesa 10","Mesa 11","Mesa 12",
                                          "Mesa 13","Mesa 14","Mesa 15","Mesa 16",
                                          "Mesa 17","Mesa 18","Mesa 19","Mesa 20",
                                          "Mesa 21","Mesa 22","Mesa 23","Mesa 24",
                                          "Mesa 25","Mesa 26","Mesa 27","Mesa 28",
                                          "Mesa 29","Mesa 30"};
    private int item_escolhido=1;

    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String STATUS_PED = "status_pedido";
    private static final String QUANT_PESSOAS = "quant_pessoas";
    private static final int NOVO_PED = 0;
    private String PREFERENCES = "preferences_user";
    private String REMEMBER_NOME_USER = "user_name_remember";
    private TinyDB tinyDB;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intent intent = new Intent(this, ServiceTest.class);
        //startService(intent);

        //Eventbus register
        EventBus.getDefault().register(this);

        //notificarUsuario();

        //Intent intent = new Intent(this, ServiceTest.class);
        //startService(intent);

        context = getBaseContext();
        controller = new RestauranteController(context);

        controller.backupBancoDeDados();

        //final SharedPreferences preferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        //String nomeUser = preferences.getString(REMEMBER_NOME_USER,"");
        //UtilRestaurante.showMensagem(getApplicationContext(),"Seja bem vindo ao sistema: "+nomeUser);

        final boolean[] b1 = {false};
        final boolean[] b2 = {false};

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// apenas sincroniza os pedidos, e no início do app ele sincroniza tudo
                //verifica se o wifi está ao menos conectado
                wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                int ip = info.getIpAddress();

                if (ip != 0) {

                    ListarAsyncTask taskIng = new ListarAsyncTask("ingredientes", MainActivity.this, false, new ListarAsyncTask.AsynResponse() {
                        @Override
                        public void processFinish(boolean output) {
                            b1[0] = output;

                            if (b1[0] && b2[0]) {
                                FragmentManager transaction = getSupportFragmentManager();
                                pedidosFragment = new PedidosFragment();
                                cozinhaFragment = new CozinhaFragment();
                                estoqueFragment = new EstoqueFragment();

                                if (navigationView.getMenu().getItem(0).isChecked()) {//pedidos selecionados
                                    transaction.beginTransaction().replace(frameLayout.getId(), pedidosFragment).commit();
                                } else if (navigationView.getMenu().getItem(1).isChecked()) {
                                    transaction.beginTransaction().replace(frameLayout.getId(), cozinhaFragment).commit();
                                } else if (navigationView.getMenu().getItem(2).isChecked()) {
                                    transaction.beginTransaction().replace(frameLayout.getId(), estoqueFragment).commit();
                                }
                            }
                        }
                    });
                    ListarAsyncTask taskPedido = new ListarAsyncTask("pedido", MainActivity.this, true, new ListarAsyncTask.AsynResponse() {
                        @Override
                        public void processFinish(boolean output) {

                            b2[0] = output;

                            if (b1[0] && b2[0]) {
                                FragmentManager transaction = getSupportFragmentManager();
                                pedidosFragment = new PedidosFragment();
                                cozinhaFragment = new CozinhaFragment();
                                estoqueFragment = new EstoqueFragment();

                                if (navigationView.getMenu().getItem(0).isChecked()) {//pedidos selecionados
                                    transaction.beginTransaction().replace(frameLayout.getId(), pedidosFragment).commit();
                                } else if (navigationView.getMenu().getItem(1).isChecked()) {
                                    transaction.beginTransaction().replace(frameLayout.getId(), cozinhaFragment).commit();
                                } else if (navigationView.getMenu().getItem(2).isChecked()) {
                                    transaction.beginTransaction().replace(frameLayout.getId(), estoqueFragment).commit();
                                }
                            }
                        }
                    });
                    taskIng.execute();
                    taskPedido.execute();
                } else {
                    UtilRestaurante.showMensagem(getApplicationContext(),"Por favor, conecte-se a alguma rede Wi-Fi.");
                }

                chamarBR();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);//deixa checked o navdrawer da cozinha
        navigationView.setNavigationItemSelectedListener(this);

        frameLayout = findViewById(R.id.frameLayout);

        tinyDB = new TinyDB(getApplicationContext());

        bd = getIntent().getExtras();
        getIntent().removeExtra("firstLoad");
        if (bd != null) {
            carregaFrag = bd.getInt("firstLoad", 0);

            if (carregaFrag > 0) {
                pedidosFragment = new PedidosFragment();
                cozinhaFragment = new CozinhaFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(frameLayout.getId(), cozinhaFragment);
                transaction.commit();

            }
        }

        //runnable.run();
    }

    @Subscribe
    public void onEventeMainThread(MessageEB mMessageEb){
        Log.i("Script","MainActivity.this.onEventeMainThread()");

        if (!mMessageEb.getClassTester().equalsIgnoreCase(MainActivity.class+""))
            return;

        if (mMessageEb.getList() != null){
            UtilRestaurante.showMensagem(getApplicationContext(),"Quantidade de prato: " +
                    mMessageEb.getList().get(0).getQuant_prato() + "\nPrato: "+mMessageEb.getList().get(0).getPrato());
        }
    }

    @Subscribe
    public void onEvent(MessageEB mMessageEb){
        Log.i("Script","MainActivity.this.onEvent()");

        if (!mMessageEb.getClassTester().equalsIgnoreCase(MainActivity.class+""))
            return;

        if (mMessageEb.getNumber()>=0){
            Log.i("Script","MainActivity.this.onEvent().number: "+mMessageEb.getNumber());
        }

        if (mMessageEb.getText() != null){
            Log.i("Script","MainActivity.this.onEvent().text: "+mMessageEb.getText());
        }
    }

    Runnable runnable = new Runnable() {//Código para criar um timer e rodar ele a cada x segundos até que o app seja fechado
        @Override
        public void run() {
            Log.i("RunnableTest","Testando o runnable");
            handler.postDelayed(runnable,1000);
        }
    };

    @Override
    protected void onResume() { // toda vez que esta tela for recarrega é necessário limpar os registros de pedidos antigos
        super.onResume();
        tinyDB.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this,ServiceTest.class);
        stopService(intent);

        //Eventbus unregister
        EventBus.getDefault().unregister(this);

        //Intent intent = new Intent(this, ServiceIntent.class);
        //intent.putExtra("desligar",1);
        //startService(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            Intent it = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(it);
            finish();
        } else if (id == R.id.action_add && navigationView.getMenu().getItem(0).isChecked()) {

            item_escolhido = Integer.parseInt(itens[0].substring(itens[0].length()-2,itens[0].length()));

            AlertDialog.Builder dialog = new AlertDialog.Builder(this); //Cria uma Alert progress
            dialog.setTitle("Escolha a mesa para o pedido"); //Configura título e mensagem
            dialog.setCancelable(false); //Configura o cancelamento
            dialog.setIcon(R.drawable.ic_restaurant_menu_black_24dp); //Configura ícone

            LayoutInflater inflater = getLayoutInflater();//obtém o layout
            View dialogView = inflater.inflate(R.layout.number_picker_dialog,null); //pega o layout e infla nele a tela do numberpicker transformando tudo em uma view

            final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);

            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(20);
            numberPicker.setWrapSelectorWheel(false);

            final AlertDialog.Builder dialogNumPessoas = new AlertDialog.Builder(this);

            dialogNumPessoas.setTitle("Pessoas na mesa");
            dialogNumPessoas.setIcon(R.drawable.ic_restaurant_menu_black_24dp);
            dialogNumPessoas.setMessage("Selecione a quantidade de pessoas na mesa");
            dialogNumPessoas.setCancelable(false);
            dialogNumPessoas.setView(dialogView);

            dialog.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() { //Cria a lista de seleção
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    item_escolhido = Integer.parseInt(itens[i].substring(itens[i].length()-2,itens[i].length())); //Pega só os últimos dois caracteres
                }
            });

            dialog.setPositiveButton("Escolher", new DialogInterface.OnClickListener() {//Evento para botão de escolher
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    tinyDB.putInt(MESA_ESCOLHIDA,item_escolhido);//Salva mesa escolhida no SharedPreferences
                    tinyDB.putInt(STATUS_PED,NOVO_PED);//Salva se o pedido fará um insert ou update

                    dialogNumPessoas.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            tinyDB.putString(QUANT_PESSOAS,String.valueOf(numberPicker.getValue()));

                            Intent it = new Intent(getApplicationContext(),AdicionarNomeClienteActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(it);
                        }
                    });

                    dialogNumPessoas.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    ArrayList<Pedidos> pedidos = controller.listarNomesPorMesaPedido(String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA)));

                    if (pedidos.size()>0){// se já houverem pedidos para a mesa em questão, então não mostra nada
                        Intent it = new Intent(getApplicationContext(),AdicionarNomeClienteActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(it);
                    } else {// se não houverem pedidos para essa mes, então pede para escolher a quantidade de pessoas
                        dialogNumPessoas.create();
                        dialogNumPessoas.show();
                    }
                }
            });

            dialog.setNeutralButton("Cancelar", null);//Cria botão de cancelar

            dialog.create();//Criar o AlertDialog
            dialog.show();//Exibir o AlertDialog
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pedidos) {

            pedidosFragment =  new PedidosFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayout.getId(),pedidosFragment);
            transaction.commit();
        } else if (id == R.id.nav_cozinha) {

            cozinhaFragment =  new CozinhaFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayout.getId(),cozinhaFragment);
            transaction.commit();
        } else if (id == R.id.nav_estoque) {

            estoqueFragment =  new EstoqueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayout.getId(),estoqueFragment);
            transaction.commit();
        //} else if (id == R.id.new_cadastro) {
        //    cadastroFragment =  new CadastroFragment();
        //    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //    transaction.replace(frameLayout.getId(),cadastroFragment);
        //    transaction.commit();
        } else if (id == R.id.nav_sobre){
            sobreFragment = new SobreFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayout.getId(),sobreFragment);
            transaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void notificarUsuario(){

        Intent ii = new Intent(getApplicationContext(), AdicionarNomeClienteActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.Builder notification_builder;
        NotificationManager notification_manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //String chanel_id = "3000"+new Random().nextInt(100);
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.setVibrationPattern(new long[]{500,500,250,250});
            mChannel.enableVibration(true);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),att);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notification_manager.createNotificationChannel(mChannel);
            notification_builder = new NotificationCompat.Builder(this, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(this);
            notification_builder.setVibrate(new long[]{500,500,250,250});
            notification_builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        //NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        //bigText.setBigContentTitle("Novo pedido lançado");
        //bigText.bigText("Novo pedido para mesa 10");
        //bigText.setSummaryText("Aplicativo"); ---- fica bem legal com esse cara, mas por conta de compatibilidade nao usarei
        //mBuilder.setStyle(bigText);

        notification_builder.setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_bruxellas_notify))
                .setContentTitle("Notification Title")
                .setContentText("Notification Body")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notification_manager.notify(new Random().nextInt(100),notification_builder.build());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void chamarBR(){

        //Intent intent = new Intent("BROADCAST_RECEIVER_TEST");
        //sendBroadcast(intent);

        Log.i("Script","chamarBR");

        /*MessageEB m = new MessageEB();
        m.setClassTester(ServiceTest.class+"");
        //m.setText("This message came from ActivyMain");

        EventBus.getDefault().post(m);*/

        //Intent it = new Intent();
        //it.setAction(ServiceTest.FILTRO_KEY);
        //it.putExtra(ServiceTest.MENSAGEM_KEY,"ServiceTest: mensagem ok.<br>");
        //LocalBroadcastManager.getInstance(this).sendBroadcast(it);

        //Intent intent = new Intent(this, ServiceTest.class);
        //Intent intent = new Intent(this, ServiceIntent.class);
        //startService(intent);

        ComponentName cp = new ComponentName(this, JobSchedulerService.class);

        PersistableBundle b = new PersistableBundle();
        b.putString("string","Qualquer coisa");

        JobInfo jb = new JobInfo.Builder(1,cp)
                .setBackoffCriteria(50,JobInfo.BACKOFF_POLICY_LINEAR)
                .setExtras(b)
                //.setMinimumLatency(2000)
                //.setOverrideDeadline(2000)
                .setPersisted(true)
                .setPeriodic(20)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();

        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(jb);

        Log.i("Script","TESTANDO");

    }

}
