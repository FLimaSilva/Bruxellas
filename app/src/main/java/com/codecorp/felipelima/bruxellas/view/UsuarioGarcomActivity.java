package com.codecorp.felipelima.bruxellas.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.fragments.CozinhaFragment;
import com.codecorp.felipelima.bruxellas.fragments.PedidosFragment;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.util.ListarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class UsuarioGarcomActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    PedidosFragment pedidosFragment;

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

    private RestauranteController controller;

    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String STATUS_PED = "status_pedido";
    private static final String QUANT_PESSOAS = "quant_pessoas";
    private static final int NOVO_PED = 0;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_garcom);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.frameLayoutPedido);

        controller = new RestauranteController(getApplicationContext());

        tinyDB = new TinyDB(getApplicationContext());

        UtilRestaurante.configuraStatusBar(UsuarioGarcomActivity.this,getSupportActionBar(),"Bruxellas",false);

        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verifica se o wifi está ao menos conectado
                wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                int ip = info.getIpAddress();

                if (ip != 0) {
                    ListarAsyncTask taskPedido = new ListarAsyncTask("pedido", UsuarioGarcomActivity.this, true, new ListarAsyncTask.AsynResponse() {
                        @Override
                        public void processFinish(boolean output) {
                            FragmentManager transaction = getSupportFragmentManager();
                            pedidosFragment = new PedidosFragment();

                            transaction.beginTransaction().replace(frameLayout.getId(), pedidosFragment).commit();
                        }
                    });
                    taskPedido.execute();
                } else {
                    UtilRestaurante.showMensagem(getApplicationContext(),"Por favor, conecte-se a alguma rede Wi-Fi.");
                }
            }
        });

        pedidosFragment = new PedidosFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), pedidosFragment);
        transaction.commit();
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
            Intent it = new Intent(UsuarioGarcomActivity.this,LoginActivity.class);
            startActivity(it);
            finish();
        } else if (id == R.id.action_add) {

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
