package com.codecorp.felipelima.bruxellas.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.PratosAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EscolherPratoActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Pratos> dados = new ArrayList<>();
    private ArrayList<Pratos> pratosPedidos;
    private ArrayList<AdicionalRetiradaPedido> adicionalRetiradaPedido;
    private EditText editTextFiltroPrato;
    private JsonArray estoquePratos;

    private static final String LISTA_PRATOS = "lista_pratos_pedidos";
    private static final String LISTA_AD_RE_PEDIDO = "lista_ingredientes_AdRe_pedido";
    private static final String LISTA_ESTOQUE = "lista_estoque_pedido";
    private static final String PED_COZ = "cozinha";
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_prato);

        tinyDB = new TinyDB(getApplicationContext());

        //retorna a lista que está no sharedPreferences com os pratos pedidos
        if (tinyDB.containsKey(LISTA_PRATOS)){
            pratosPedidos = tinyDB.getListPratos(LISTA_PRATOS);
        }
        else {
            pratosPedidos = new ArrayList<>();
        }

        //retorna a lista que está no sharedPreferences com os Item lançados para o estoque
        if (tinyDB.containsKey(LISTA_ESTOQUE)){
            estoquePratos = tinyDB.getListEstoque(LISTA_ESTOQUE);
        } else {
            estoquePratos = new JsonArray();
        }

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        UtilRestaurante.configuraStatusBar(EscolherPratoActivity.this,getSupportActionBar(),"Escolha o prato desejado");

        listView = findViewById(R.id.listViewPrato);
        editTextFiltroPrato = findViewById(R.id.editTextFiltroPrato);

        RestauranteController controller = new RestauranteController(getApplicationContext());
        dados = controller.listarPratos(); //Faz listagem com todos os itens quando tela é carregada
        atualizaList(); //Atualiza listview com a listagem anterior

        editTextFiltroPrato.addTextChangedListener(new TextWatcher() { //Caso ocorra mudança na digitação do filtro
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RestauranteController controller = new RestauranteController(getApplicationContext());
                dados = controller.listarPratos(charSequence.toString());
                atualizaList();//Atualizar a lista com os itens que foram filtrados no banco de dados
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int position = i;//linha em que ocorreu o click

                final AlertDialog.Builder dialogAdRe = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert progress
                dialogAdRe.setTitle("Deseja adicionar ou retirar ingrediente do prato?"); //Configura título e mensagem
                dialogAdRe.setCancelable(false); //Configura o cancelamento
                dialogAdRe.setIcon(R.drawable.ic_kitchen_black_24dp); //Configura ícone

                final AlertDialog.Builder dialogQuant = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert progress

                dialogAdRe.setPositiveButton("Sim", new DialogInterface.OnClickListener() {//Evento para botão de escolher
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dados.get(position).setStatus(PED_COZ);
                        dados.get(position).setQuant("1");
                        pratosPedidos.add(dados.get(position));
                        tinyDB.putListPratos(LISTA_PRATOS,pratosPedidos);

                        JsonObject pratosPed = new JsonObject();
                        pratosPed.addProperty("prato",dados.get(position).getNome());//insere o nome do prato
                        pratosPed.addProperty("quantidade","1");
                        estoquePratos.add(pratosPed);
                        tinyDB.putListEstoque(LISTA_ESTOQUE, estoquePratos);

                        Intent intent = new Intent(getApplicationContext(),EscolherAdReActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                dialogAdRe.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
                            adicionalRetiradaPedido = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);
                        } else {
                            adicionalRetiradaPedido = new ArrayList<>();
                        }

                        final AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();
                        adRe.setAdicional("");
                        adRe.setRetirada("");

                        //código novo para a quantidade
                        LayoutInflater inflater = getLayoutInflater();//obtém o layout
                        View dialogView = inflater.inflate(R.layout.number_picker_dialog,null); //pega o layout e infla nele a tela do numberpicker transformando tudo em uma view

                        final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);//Localiza o numberPicker que está dentro da view
                        numberPicker.setMinValue(1);
                        numberPicker.setMaxValue(10);
                        numberPicker.setWrapSelectorWheel(false);

                        dialogQuant.setTitle("Quantidade do prato");
                        dialogQuant.setIcon(android.R.drawable.ic_input_add);
                        dialogQuant.setMessage("Escolha a quantidade do prato montado:");
                        dialogQuant.setCancelable(false);

                        dialogQuant.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dados.get(position).setQuant(String.valueOf(numberPicker.getValue()));
                                dados.get(position).setStatus(PED_COZ);
                                pratosPedidos.add(dados.get(position));
                                adicionalRetiradaPedido.add(adRe);

                                JsonObject pratosPed = new JsonObject();
                                pratosPed.addProperty("prato",dados.get(position).getNome());
                                pratosPed.addProperty("quantidade",numberPicker.getValue());
                                estoquePratos.add(pratosPed);

                                tinyDB.putListPratos(LISTA_PRATOS,pratosPedidos);
                                tinyDB.putListAdRePedido(LISTA_AD_RE_PEDIDO,adicionalRetiradaPedido);
                                tinyDB.putListEstoque(LISTA_ESTOQUE, estoquePratos);

                                Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(it);
                                finish();
                            }
                        });

                        dialogQuant.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                    }
                });

                dialogAdRe.create();//Criar o AlertDialog
                dialogAdRe.show();//Exibir o AlertDialog

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //ID do botão gerado automaticamente pelo android
                Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(getApplicationContext(),AdicionarPratoActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
        super.onBackPressed();
    }

    private void atualizaList(){
        PratosAdapter adapter = new PratosAdapter(getApplicationContext(),dados);
        listView.setAdapter(adapter);
    }

}
