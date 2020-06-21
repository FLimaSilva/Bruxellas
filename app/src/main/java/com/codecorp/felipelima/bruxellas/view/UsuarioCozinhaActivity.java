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
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class UsuarioCozinhaActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    CozinhaFragment cozinhaFragment;

    private WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_cozinha);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.frameLayoutCozinha);

        UtilRestaurante.configuraStatusBar(UsuarioCozinhaActivity.this,getSupportActionBar(),"Bruxellas",false);

        FloatingActionButton fab = findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verifica se o wifi está ao menos conectado
                wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                int ip = info.getIpAddress();

                if (ip != 0) {
                    ListarAsyncTask taskPedido = new ListarAsyncTask("pedido", UsuarioCozinhaActivity.this, true, new ListarAsyncTask.AsynResponse() {
                        @Override
                        public void processFinish(boolean output) {
                            FragmentManager transaction = getSupportFragmentManager();
                            cozinhaFragment = new CozinhaFragment();

                            transaction.beginTransaction().replace(frameLayout.getId(), cozinhaFragment).commit();
                        }
                    });
                    taskPedido.execute();
                } else {
                    UtilRestaurante.showMensagem(getApplicationContext(),"Por favor, conecte-se a alguma rede Wi-Fi.");
                }
            }
        });

        cozinhaFragment = new CozinhaFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), cozinhaFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            Intent it = new Intent(UsuarioCozinhaActivity.this,LoginActivity.class);
            startActivity(it);
            finish();
        } else if (id == R.id.action_info) {
            Intent it = new Intent(UsuarioCozinhaActivity.this,InfoCozinhaActivity.class);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
