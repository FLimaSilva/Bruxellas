package com.codecorp.felipelima.bruxellas.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.codecorp.felipelima.bruxellas.adapter.BebidasAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EscolherBebidaActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editTextFiltroBebida;
    private ArrayList<Bebidas> dados = new ArrayList<>();
    private ArrayList<Bebidas> bebidasPedidas;
    private JsonArray bebidasEstoque;

    private static final String LISTA_BEBIDAS = "lista_bebidas_pedidas";
    private static final String LISTA_ESTOQUE_BEBIDAS = "lista_estoque_pedido_bebidas";
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_bebida);

        tinyDB = new TinyDB(getApplicationContext());

        //retorna a lista que está no sharedPreferences com os pratos pedidos
        if (tinyDB.containsKey(LISTA_BEBIDAS)){
            bebidasPedidas = tinyDB.getListBebidas(LISTA_BEBIDAS);
        }
        else {
            bebidasPedidas = new ArrayList<>();
        }

        //retorna a lista que está no sharedPreferences com os Item lançados para o estoque
        if (tinyDB.containsKey(LISTA_ESTOQUE_BEBIDAS)){
            bebidasEstoque = tinyDB.getListEstoque(LISTA_ESTOQUE_BEBIDAS);
        } else {
            bebidasEstoque = new JsonArray();
        }

        UtilRestaurante.configuraStatusBar(EscolherBebidaActivity.this,getSupportActionBar(),"Escolha a bebida desejada");

        listView = findViewById(R.id.listViewBebida);
        editTextFiltroBebida = findViewById(R.id.editTextFiltroBebida);

        RestauranteController controller = new RestauranteController(getApplicationContext());
        dados = controller.listarBebidas(); //Faz listagem com todos os itens quando tela é carregada
        atualizaList(); //Atualiza listview com a listagem anterior

        editTextFiltroBebida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RestauranteController controller = new RestauranteController(getApplicationContext());
                dados = controller.listarBebidas(charSequence.toString());
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

                final AlertDialog.Builder dialogQuant = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert progress

                //código novo para a quantidade
                LayoutInflater inflater = getLayoutInflater();//obtém o layout
                View dialogView = inflater.inflate(R.layout.number_picker_dialog,null); //pega o layout e infla nele a tela do numberpicker transformando tudo em uma view

                final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);//Localiza o numberPicker que está dentro da view
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(10);
                numberPicker.setWrapSelectorWheel(false);

                dialogQuant.setTitle("Quantidade da bebida");
                dialogQuant.setIcon(android.R.drawable.ic_input_add);
                dialogQuant.setMessage("Escolha a quantidade da bebida selecionada:");
                dialogQuant.setCancelable(false);

                dialogQuant.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dados.get(position).setQuant(String.valueOf(numberPicker.getValue()));
                        bebidasPedidas.add(dados.get(position));
                        tinyDB.putListBebidas(LISTA_BEBIDAS,bebidasPedidas);

                        JsonObject bebidasPed = new JsonObject();
                        bebidasPed.addProperty("bebida",dados.get(position).getNome());
                        bebidasPed.addProperty("quantidade",numberPicker.getValue());
                        bebidasEstoque.add(bebidasPed);
                        tinyDB.putListEstoque(LISTA_ESTOQUE_BEBIDAS, bebidasEstoque);

                        Intent it = new Intent(getApplicationContext(),AdicionarBebidaActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(it);
                        finish();
                    }
                });

                dialogQuant.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent it = new Intent(getApplicationContext(),AdicionarBebidaActivity.class);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //ID do botão gerado automaticamente pelo android
                Intent it = new Intent(getApplicationContext(),AdicionarBebidaActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent it = new Intent(getApplicationContext(),AdicionarBebidaActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);

        super.onBackPressed();
    }

    private void atualizaList(){
        BebidasAdapter adapter = new BebidasAdapter(getApplicationContext(),dados);
        listView.setAdapter(adapter);
    }
}
