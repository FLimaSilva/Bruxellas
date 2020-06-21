package com.codecorp.felipelima.bruxellas.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.AdReAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetirada;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EscolherAdReActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editTextFiltroAdRe;
    private TextView textTipo;
    private Switch switchTipo;
    private String filtroTipo;

    private ArrayList<Ingredientes> dados = new ArrayList<>();

    private static final String LISTA_PRATOS = "lista_pratos_pedidos";
    private static final String LISTA_AD_RE = "lista_ingredientes_AdRe";
    private static final String LISTA_AD_RE_PEDIDO = "lista_ingredientes_AdRe_pedido";
    private static final String LISTA_ESTOQUE = "lista_estoque_pedido";
    private ArrayList<AdicionalRetirada> adicionalRetirada;
    private ArrayList<AdicionalRetiradaPedido> adicionalRetiradaPedido;
    private TinyDB tinyDB;
    private RestauranteController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_ad_re);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        UtilRestaurante.configuraStatusBar(EscolherAdReActivity.this,getSupportActionBar(),"Escolha os ingredientes");

        listView = findViewById(R.id.listViewAdRt);
        editTextFiltroAdRe = findViewById(R.id.editTextFiltroAdRt);
        switchTipo = findViewById(R.id.switchTipo);
        textTipo = findViewById(R.id.textTipo);

        tinyDB = new TinyDB(getApplicationContext());

        controller = new RestauranteController(getApplicationContext());


        verificaSwitch(switchTipo.isChecked());
        RestauranteController controller = new RestauranteController(getApplicationContext());
        dados = controller.listarIngredientes(filtroTipo); //Faz listagem com todos os itens quando tela é carregada
        atualizaList(); //Atualiza listview com a listagem anterior

        switchTipo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                verificaSwitch(b);
                RestauranteController controller = new RestauranteController(getApplicationContext());
                dados = controller.listarIngredientes(filtroTipo); //Faz listagem com todos os itens quando tela é carregada
                atualizaList(); //Atualiza listview com a listagem anterior
            }
        });

        editTextFiltroAdRe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                verificaSwitch(switchTipo.isChecked());
                RestauranteController controller = new RestauranteController(getApplicationContext());
                dados = controller.listarIngredientes(filtroTipo,charSequence.toString()); //Faz listagem com todos os itens quando tela é carregada
                atualizaList(); //Atualiza listview com a listagem anterior
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //ID do botão gerado automaticamente pelo android
                AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();
                adRe.setAdicional("");
                adRe.setRetirada("");

                if (tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
                    adicionalRetiradaPedido = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);
                } else {
                    adicionalRetiradaPedido = new ArrayList<>();
                }

                adicionalRetiradaPedido.add(adRe);

                tinyDB.putListAdRePedido(LISTA_AD_RE_PEDIDO,adicionalRetiradaPedido);
                tinyDB.remove(LISTA_AD_RE);

                Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                finish();
                break;
            case R.id.action_save_itens2:

                tinyDB = new TinyDB(getApplicationContext());
                AlertDialog.Builder dialogQuant = new AlertDialog.Builder(this); //Cria uma Alert progress

                LayoutInflater inflater = getLayoutInflater();//obtém o layout
                View dialogView = inflater.inflate(R.layout.number_picker_dialog,null); //pega o layout e infla nele a tela do numberpicker transformando tudo em uma view

                final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);//Localiza o numberPicker que está dentro da view
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(10);
                numberPicker.setWrapSelectorWheel(false);

                if (tinyDB.containsKey(LISTA_AD_RE)){
                    adicionalRetirada = tinyDB.getListAdRe(LISTA_AD_RE);

                    String adIng = "";
                    String reIng = "";

                    for (AdicionalRetirada ad: adicionalRetirada) {
                        if (ad.getQuant_ingrediente()==1){ // Adicional
                            Ingredientes ingAd = controller.listarIngredientes(ad.getId_ingrediente()).get(0);
                            //adIng = adIng + String.valueOf(ad.getId_ingrediente()) + ",";///Aqui estava salvando os ad pelo id
                            adIng = adIng + String.valueOf(ingAd.getNome()) + ",";///Agora está pelo nome
                        }
                        else if (ad.getQuant_ingrediente()==-1){ //Retirada
                            Ingredientes ingRe = controller.listarIngredientes(ad.getId_ingrediente()).get(0);
                            //reIng = reIng + String.valueOf(ad.getId_ingrediente()) + ",";///Aqui estava salvando os ad pelo id
                            reIng = reIng + String.valueOf(ingRe.getNome()) + ",";///Agora está pelo nome
                        }
                        //System.out.println("Id_ingrediente:"+ad.getId_ingrediente()+" - Quant:"+ad.getQuant_ingrediente());
                    }

                    if (adIng.length()>1) {
                        adIng = adIng.substring(0, adIng.length() - 1);
                    }
                    if (reIng.length()>1){
                        reIng = reIng.substring(0,reIng.length() - 1);
                    }

                    if (tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
                        adicionalRetiradaPedido = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);
                    } else {
                        adicionalRetiradaPedido = new ArrayList<>();
                    }

                    final AdicionalRetiradaPedido adRePed = new AdicionalRetiradaPedido();
                    adRePed.setAdicional(adIng);
                    adRePed.setRetirada(reIng);

                    tinyDB.remove(LISTA_AD_RE);

                    dialogQuant.setTitle("Quantidade do prato");
                    dialogQuant.setIcon(android.R.drawable.ic_input_add);
                    dialogQuant.setMessage("Escolha a quantidade do prato montado:");
                    dialogQuant.setCancelable(false);

                    dialogQuant.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int quantPratos = numberPicker.getValue();

                            ArrayList<Pratos> pratosPedidos = tinyDB.getListPratos(LISTA_PRATOS);
                            Pratos lastPrato = pratosPedidos.get(pratosPedidos.size()-1);
                            pratosPedidos.remove(pratosPedidos.size()-1);
                            lastPrato.setQuant(String.valueOf(quantPratos));
                            pratosPedidos.add(lastPrato);

                            adicionalRetiradaPedido.add(adRePed);

                            JsonArray ingsEstoque = tinyDB.getListEstoque(LISTA_ESTOQUE);
                            JsonObject itemEstoque = new JsonObject();
                            ingsEstoque.remove(ingsEstoque.size()-1);

                            itemEstoque.addProperty("prato",lastPrato.getNome());
                            itemEstoque.addProperty("quantidade",quantPratos);


                            if (!adRePed.getAdicional().equals("")){
                                itemEstoque.addProperty("adicional",adRePed.getAdicional());
                                //itemEstoque.addProperty("quantidadeAd",quantPratos);
                            }

                            if (!adRePed.getRetirada().equals("")){
                                itemEstoque.addProperty("retirada",adRePed.getRetirada());
                                itemEstoque.addProperty("quantidadeRe",-quantPratos);
                            }

                            ingsEstoque.add(itemEstoque);

                            tinyDB.putListAdRePedido(LISTA_AD_RE_PEDIDO,adicionalRetiradaPedido);
                            tinyDB.putListPratos(LISTA_PRATOS,pratosPedidos);
                            tinyDB.putListEstoque(LISTA_ESTOQUE,ingsEstoque);

                            Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(it);
                            finish();
                        }
                    });

                    dialogQuant.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<Pratos> pratosPedidos = tinyDB.getListPratos(LISTA_PRATOS);
                            pratosPedidos.remove(pratosPedidos.size()-1);
                            tinyDB.putListPratos(LISTA_PRATOS,pratosPedidos);

                            dialogInterface.dismiss();

                            Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(it);
                            finish();
                        }
                    });

                    dialogQuant.setView(dialogView);

                    dialogQuant.create();
                    dialogQuant.show();

                } else {
                    UtilRestaurante.showMensagem(getApplicationContext(),"Você ainda não selecionou nenhum adicional ou retirada!");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();
        adRe.setAdicional("");
        adRe.setRetirada("");

        if (tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
            adicionalRetiradaPedido = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);
        } else {
            adicionalRetiradaPedido = new ArrayList<>();
        }

        adicionalRetiradaPedido.add(adRe);
        tinyDB.remove(LISTA_AD_RE);

        tinyDB.putListAdRePedido(LISTA_AD_RE_PEDIDO,adicionalRetiradaPedido);

        Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
        super.onBackPressed();
    }

    private void atualizaList(){
        AdReAdapter adapter = new AdReAdapter(getApplicationContext(),dados);
        listView.setAdapter(adapter);
    }

    private void verificaSwitch (boolean b){
        if (b){
            filtroTipo = switchTipo.getTextOn().toString();
        } else {
            filtroTipo = switchTipo.getTextOff().toString();
        }
        textTipo.setText(filtroTipo);
    }

}
