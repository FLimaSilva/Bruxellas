package com.codecorp.felipelima.bruxellas.view;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.AplicativoController;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.services.ServiceTest;
import com.codecorp.felipelima.bruxellas.util.ListarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 100;
    private static final int TIME_BAR_CARR = 3000;
    private TinyDB tinyDB;
    private WifiManager wifi;
    private String PREFERENCES = "preferences_user";
    private String DIMENSION_GRID_VIEW_HEIGHT = "gridview_height";
    private String DIMENSION_GRID_VIEW_WIDTH = "gridview_width";
    private String REMEMBER_NIVEL_USER = "user_remember";
    private static final String LISTA_ESTOQUE_DEL = "lista_estoque_pedido_deleta";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private int carregamento;

    private ProgressBar progressBar;
    private TextView textPerCent;

    private ArrayList<Pratos> pratosPedidos;
    private ArrayList<AdicionalRetiradaPedido> adRePedidos;

    public static final int APP_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pratosPedidos = new ArrayList<>();
        adRePedidos = new ArrayList<>();

        carregamento = 0;

        tinyDB = new TinyDB(getApplicationContext());
        tinyDB.clear();

        if (tinyDB.containsKey(LISTA_ESTOQUE_DEL)) {
            JsonArray pratosPed = tinyDB.getListEstoque(LISTA_ESTOQUE_DEL);
            for (int i = 0; i < pratosPed.size(); i++) {
                boolean auxAdRe = false;

                JsonObject object = pratosPed.get(i).getAsJsonObject();
                Pratos prato = new Pratos();
                AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();

                prato.setNome(object.get("prato").getAsString());
                prato.setQuant(object.get("quantidade").getAsString());

                if (object.has("adicional")){ //(!object.get("adicional").getAsString().equals("")) {
                    adRe.setAdicional(object.get("adicional").getAsString());
                    adRe.setQuant_adicional(object.get("quantidade").getAsString());
                    auxAdRe = true;
                }

                if (object.has("retirada")){ //(!object.get("retirada").getAsString().equals("")) {
                    adRe.setRetirada(object.get("retirada").getAsString());
                    adRe.setQuant_retirada(object.get("quantidadeRe").getAsString());
                    auxAdRe = true;
                }

                if (auxAdRe){
                    adRePedidos.add(adRe);
                }
                pratosPedidos.add(prato);
            }
        }

        progressBar = findViewById(R.id.progressBar2);
        textPerCent = findViewById(R.id.textCar);

        progressBar.setProgress(carregamento);
        textPerCent.setText(String.valueOf(carregamento)+"%");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));//pinta a status bar de preto
        } else {
            UtilRestaurante.showMensagem(getApplicationContext(),"Incompatibilidade com este dispositivo!");
        }

        //Salva as dimensões no shared preferences para celular do usuário
        preferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        if ((!preferences.contains(DIMENSION_GRID_VIEW_HEIGHT)) || (!preferences.contains(DIMENSION_GRID_VIEW_WIDTH))) {
            editor = preferences.edit();
            editor.putInt(DIMENSION_GRID_VIEW_HEIGHT, tamanhosGridView(true));
            editor.putInt(DIMENSION_GRID_VIEW_WIDTH, tamanhosGridView(false));
            editor.commit();
        }

        carregamento=300;
        updateProgress(carregamento);

        if (AplicativoController.verificarGooglePlayServices(SplashActivity.this)) {//verifica se o google play services está instalado corretamente no dispositivo
            if (temPermissaoExternalStorage()) {//verifica se tem a permissão para escrita e leitura dos arquivos do celular
                carregamento=600;
                updateProgress(carregamento);
                sincronizacaoPermissaoBd();
            }
        } else {
            UtilRestaurante.showMensagem(getApplicationContext(), "Google Play Services não configurado!!");
        }

        //new Thread(runnable).start();
    }

    /*Runnable runnable = new Runnable() {//Código para criar um timer e rodar ele a cada x segundos até que o app seja fechado
        @Override
        public void run() {
            mActive = true;
            try{
                int waited = 0;
                while (mActive && (waited<TIME_BAR_CARR)){
                    Thread.sleep(200);
                    if (mActive){
                        waited += 200;
                        updateProgress(waited);
                    }
                }
            } catch (Exception e){
                Log.e("Error",e.getMessage());
            } finally {
               onContinue(loginShared());//só tem um start activity(it) e finnish();
            }
        }
    };*/

    private void updateProgress (int update){
        final int progress = progressBar.getMax() * update / TIME_BAR_CARR;
        progressBar.setProgress(progress);
        textPerCent.setText(progress+"%");
    }

    private void apresentarTelaSplash(final Intent it){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(it);
                finish();
            }
        },SPLASH_TIME_OUT);

    }

    private boolean temPermissaoExternalStorage() {

        if (Build.VERSION.SDK_INT < 23){// abaixo da api 23 as permissões são liberadas automaticamente
            return true;
        } else { // acima ou igual a api 23 é necessário efetuar procedimento de autorização das permissões

            if (checkAndRequestPermissions()){
                return true;
            } else {
                return false;
            }

        }
    }

    private boolean checkAndRequestPermissions() {

        boolean retorno = true;

        List<String> permissionsNecessary = new ArrayList<>();

        int permissionWriteExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionWifi = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);

        // -1 Negado
        // 0 Permitido

        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED){
            permissionsNecessary.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionWifi != PackageManager.PERMISSION_GRANTED){
            permissionsNecessary.add(Manifest.permission.ACCESS_WIFI_STATE);
        }

        if (!permissionsNecessary.isEmpty()){
            // Apresenta para o usuário a mensagem de solitando permissão

            ActivityCompat.requestPermissions(this,
                    permissionsNecessary.toArray(new String[permissionsNecessary.size()]),APP_PERMISSIONS_REQUEST_WRITE_STORAGE);

            retorno = false;
        }

        return retorno;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode){

            case APP_PERMISSIONS_REQUEST_WRITE_STORAGE:
                boolean sucess=true;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED){
                        sucess = true;
                        //apresentarTelaSplash();
                    }
                    else {
                        sucess = false;
                    }
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_WIFI_STATE) ==
                            PackageManager.PERMISSION_GRANTED){
                        sucess = sucess;
                    } else {
                        sucess = false;
                    }

                    if (sucess){
                        UtilRestaurante.showMensagem(getApplicationContext(),"Todos os requisitos para o funcionamento do app está ok!!");

                        sincronizacaoPermissaoBd();
                    } else {
                        finish();
                    }
                }



                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void sincronizacaoPermissaoBd(){

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        //Log.d("Wifi Info","Info: "+info.toString());
        int ip = info.getIpAddress();
        String ipAddress = String.format("%d.%d.%d.%d",(ip & 0xff),(ip >> 8 & 0xff), (ip >> 16 &0xff), (ip >> 24 &0xff));
        Log.d("Wifi Info","Info: "+ipAddress);

        if (ip != 0) {//Tem que pelo menos estar conectado em algum Wifi
            carregamento=950;
            updateProgress(carregamento);
            try {
                final ListarAsyncTask taskPedido = new ListarAsyncTask("pedido", SplashActivity.this,false, new ListarAsyncTask.AsynResponse() {
                    @Override
                    public void processFinish(boolean output) {
                        if (output){
                            carregamento=3000;
                            updateProgress(carregamento);
                            apresentarTelaSplash(loginShared());
                        }
                    }
                });
                final ListarAsyncTask taskUsuarios = new ListarAsyncTask("usuarios", SplashActivity.this,false, new ListarAsyncTask.AsynResponse() {
                    @Override
                    public void processFinish(boolean output) {
                        taskPedido.execute();
                        if (output){
                            carregamento=2550;
                            updateProgress(carregamento);
                        }
                    }
                });
                final ListarAsyncTask taskBebidas = new ListarAsyncTask("bebidas", SplashActivity.this,false, new ListarAsyncTask.AsynResponse() {
                    @Override
                    public void processFinish(boolean output) {
                        taskUsuarios.execute();
                        if (output){
                            carregamento=2100;
                            updateProgress(carregamento);
                        }
                    }
                });
                final ListarAsyncTask taskIngredientes = new ListarAsyncTask("ingredientes", SplashActivity.this,false, new ListarAsyncTask.AsynResponse() {
                    @Override
                    public void processFinish(boolean output) {
                        taskBebidas.execute();
                        if (output){
                            carregamento=1700;
                            updateProgress(carregamento);
                        }
                    }
                });
                ListarAsyncTask taskPratos = new ListarAsyncTask("pratos", SplashActivity.this,false, new ListarAsyncTask.AsynResponse() {
                    @Override
                    public void processFinish(boolean output) {
                        taskIngredientes.execute();
                        if (output){
                            carregamento=1300;
                            updateProgress(carregamento);
                        }
                    }
                });

                taskPratos.execute();

            } catch (Exception e) {
                Log.e("Servidor", "Erro para se conectar ao servidor: " + e.getMessage());
                UtilRestaurante.showMensagem(getApplicationContext(), "Falha ao conectar ao servidor!\nAbra o app novamente!");
            }
        } else{
            /*View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout,"Por favor, conecte-se a alguma rede Wi-Fi.",Snackbar.LENGTH_LONG)//Snackbar.LENGTH_INDEFINITE
                    .setAction("Fechar App", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();*/
            UtilRestaurante.showMensagem(getApplicationContext(),"Por favor, conecte-se a alguma rede Wi-Fi.");
            apresentarTelaSplash(loginShared());
        }

    }

    public int tamanhosGridView(boolean widthHeight){//Se mandar false será o width, se mandar true será o height

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float pxsWidth = (float)dm.widthPixels;//obtém o valor em float do width da tela (1080)
        float pxsHeight = (float) dm.heightPixels;//obtém o valor em float do height da tela (1920)
        float density = (float)getResources().getDisplayMetrics().densityDpi;//obtém o valor em float da densidade da tela do dispositivo do usuário
        float resultFinal = 0;

        if (widthHeight){
            int statusBarHeight = 0;
            int toolBarHeight = 0;
            TypedValue tv = new TypedValue();

            int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
            if (resourceId>0){
                statusBarHeight = getResources().getDimensionPixelSize(resourceId); //pega o valor em inteiro dos pixels da statusbar
            }
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
                toolBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());//pega o valor em inteiro dos pixels da toolbar
            }

            float statusBarHeightF = (float)statusBarHeight;//transforma o valor para float dos pixels da statusbar
            float toolBarHeightF = (float)toolBarHeight;//transforma o valor para float dos pixels da toolbar

            float resultStatusBarHeight = (statusBarHeightF / (density/DisplayMetrics.DENSITY_DEFAULT));
            float resultToolBarHeight = (toolBarHeightF / (density/DisplayMetrics.DENSITY_DEFAULT));
            float resultWidth = (pxsWidth / (density/DisplayMetrics.DENSITY_DEFAULT));
            //pega o result, subtrai o valor da status bar (24), subtrai a toolbar(56), subtrai o valor dos padding (9), o restante divide por 2 e arredonda para cima (135)
            resultFinal = (resultWidth - (1*resultToolBarHeight) - resultStatusBarHeight - 9)/2;//////se o tablet tiver a barra com botões na própria tela, dá problema (multiplica por 2 o resultToolBarHeight)
        } else{
            float resultHeight = (pxsHeight / (density/DisplayMetrics.DENSITY_DEFAULT));//problema aqui é o seguinte: a tela é de 1280, por exemplo, mas quando o app pega a informação vem somente como 1232
            //pega o result, subtrai 14(padding) e depois 2(margin) em seguida divide por 3 que é o número de colunas, arredonda para 208
            resultFinal = ((resultHeight - 16)/3)-1;
        }

       DisplayMetrics metrics = getResources().getDisplayMetrics();
       float resultFinalAux = ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
       float resultFinalPX = (resultFinal * resultFinalAux)+2;

       return (int) resultFinalPX;
    }

    public Intent loginShared(){

        Intent it;

        if (preferences.contains(REMEMBER_NIVEL_USER)){
            switch (preferences.getString(REMEMBER_NIVEL_USER,"gar")){
                case "adm":
                    it = new Intent(SplashActivity.this,MainActivity.class);
                    it.putExtra("firstLoad",1);
                    break;

                case "gar":
                    it = new Intent(SplashActivity.this,UsuarioGarcomActivity.class);
                    break;

                case "coz":
                    it = new Intent(SplashActivity.this,UsuarioCozinhaActivity.class);
                    break;

                default:
                    it = new Intent(SplashActivity.this,LoginActivity.class);
                    break;
            }
        } else {
            it = new Intent(SplashActivity.this,LoginActivity.class);
        }

        return it;
    }

    public void notificarUsuario(){

        Intent ii = new Intent(getApplicationContext(), AdicionarNomeClienteActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.Builder notification_builder;
        NotificationManager notification_manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000"+new Random().nextInt(100);
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
}
