package com.codecorp.felipelima.bruxellas.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class AdicionarNomeClienteActivity extends AppCompatActivity {

    TextView textCliente,textStatus;//statusé é se o pedido é novo ou se é editado
    EditText editNomeCliente;
    String nomeCliente;

    private TinyDB tinyDB;
    private RestauranteController controller;
    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String NOME_CLIENTE = "nome_cliente";
    private static final String STATUS_PED = "status_pedido";
    private static final int NOVO_PED = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_nome_cliente);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(R.drawable.ic_launcher_background);


        editNomeCliente = findViewById(R.id.editNomeCliente);
        textCliente = findViewById(R.id.textPMesa1);
        textStatus = findViewById(R.id.textPMesa);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar); //seta a toolbar com o menu configurável

        tinyDB = new TinyDB(getApplicationContext());
        controller = new RestauranteController(getApplicationContext());

        UtilRestaurante.configuraStatusBar(AdicionarNomeClienteActivity.this,getSupportActionBar(),"Nome do cliente"); //Muda a cor da status bar para PrimaryDark
        preencheCamposPedido();// Acerta todos os campos que na teoria estão vazios

        editNomeCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nomeCliente = charSequence.toString();
                tinyDB.putString(NOME_CLIENTE,nomeCliente);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Eventos de clique nos botões do menu

        switch (item.getItemId()){
            case android.R.id.home: //ID do botão gerado automaticamente pelo android
                finish();
                break;

            case R.id.action_save_itens2:
                if (tinyDB.getString(NOME_CLIENTE).length()>1){

                    ArrayList<Pedidos> pedNome = controller.listarPedidoPorNomeMesa(String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA)),tinyDB.getString(NOME_CLIENTE));

                    if (pedNome.size()==0 || (tinyDB.getInt(STATUS_PED)>NOVO_PED)) {
                        Intent intent = new Intent(this, AdicionarPratoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    } else {
                        UtilRestaurante.showMensagem(this,"Esse nome já existe nessa mesa!!\n\n" +
                                "Por favor digite o nome de forma diferente.");
                    }
                }
                else {
                    UtilRestaurante.showMensagem(this,"Por favor digite um nome para o cliente!!");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void preencheCamposPedido(){

        if (tinyDB.containsKey(MESA_ESCOLHIDA)){
            String mesa = "mesa " + String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA));
            textCliente.setText(mesa);
        }

        if (tinyDB.containsKey(STATUS_PED)){
            if (tinyDB.getInt(STATUS_PED)>NOVO_PED){
                textStatus.setText("O pedido está sendo alterado para a ");
            } else {
                textStatus.setText("O pedido está sendo realizado para a ");
            }
        }

        if (tinyDB.containsKey(NOME_CLIENTE)){
            nomeCliente = tinyDB.getString(NOME_CLIENTE);
            editNomeCliente.setText(nomeCliente);
        }
    }

}
