package com.codecorp.felipelima.bruxellas.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.PedidoBebidasAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AdicionarBebidaActivity extends AppCompatActivity {

    private TinyDB tinyDB;
    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String NOME_CLIENTE = "nome_cliente";
    private static final String LISTA_PRATOS = "lista_pratos_pedidos";
    private static final String LISTA_BEBIDAS = "lista_bebidas_pedidas";
    private static final String LISTA_ESTOQUE_BEBIDAS = "lista_estoque_pedido_bebidas";
    private static final String LISTA_ESTOQUE_BEBIDAS_DEL = "lista_estoque_pedido_bebidas_deleta";
    private static final String STATUS_PED = "status_pedido";
    private static final int NOVO_PED = 0;

    private ArrayList<Bebidas> bebidasPedidas;
    private JsonArray deletarEstoqueBebidas;

    private TextView textMesa,textCliente, textSemBebidas,textStatus;
    private ListView listViewBebidasPedidos;
    private LinearLayout linearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_bebida);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        UtilRestaurante.configuraStatusBar(AdicionarBebidaActivity.this,getSupportActionBar(),"Escolha as bebidas");

        textMesa = findViewById(R.id.textBMesa1);
        textStatus = findViewById(R.id.textBMesa);
        textCliente = findViewById(R.id.textBCliente1);
        textSemBebidas = findViewById(R.id.textSemBebidas);
        linearList = findViewById(R.id.linearListBebidas);
        listViewBebidasPedidos = findViewById(R.id.listViewBebidasPedidas);

        tinyDB = new TinyDB(getApplicationContext());
        String mesa = "mesa " + String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA));
        String cliente = tinyDB.getString(NOME_CLIENTE);

        textMesa.setText(mesa);
        textCliente.setText(cliente);

        if (tinyDB.containsKey(LISTA_BEBIDAS)){
            bebidasPedidas = tinyDB.getListBebidas(LISTA_BEBIDAS);
            if (bebidasPedidas.size()>0){
                //seta a visibilidade do linear layout e text view (aparece p/ usuário aqui fica tua lista de bebidas)
                linearList.setVisibility(View.VISIBLE);
                textSemBebidas.setVisibility(View.GONE);
            }
        } else {
            bebidasPedidas = new ArrayList<>();
            //seta a visibilidade do linear layout e text view (aparece p/ usuário lista de bebidas)
            linearList.setVisibility(View.GONE);
            textSemBebidas.setVisibility(View.VISIBLE);
        }

        if (tinyDB.containsKey(LISTA_ESTOQUE_BEBIDAS_DEL)){
            deletarEstoqueBebidas = tinyDB.getListEstoque(LISTA_ESTOQUE_BEBIDAS_DEL);
        } else {
            deletarEstoqueBebidas = new JsonArray();
        }

        if (tinyDB.containsKey(STATUS_PED)){
            if (tinyDB.getInt(STATUS_PED)>NOVO_PED){
                textStatus.setText("O pedido está sendo alterado para a ");
            } else {
                textStatus.setText("O pedido está sendo realizado para a ");
            }
        }

        final PedidoBebidasAdapter adapter = new PedidoBebidasAdapter(getApplicationContext(),bebidasPedidas);
        listViewBebidasPedidos.setAdapter(adapter);

        listViewBebidasPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bebidasPedidas = tinyDB.getListBebidas(LISTA_BEBIDAS);

                String sPrecoBebida = UtilRestaurante.formatarValorDecimal(bebidasPedidas.get(i).getPreco_venda());
                String textDialog = bebidasPedidas.get(i).getNome() + " - R$ " + sPrecoBebida;

                AlertDialog.Builder dialogBebida = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert progress
                dialogBebida.setTitle("Bebida escolhida"); //Configura título e mensagem
                dialogBebida.setMessage(textDialog);
                dialogBebida.setCancelable(true); //Configura o cancelamento
                dialogBebida.setIcon(R.drawable.ic_local_drink_black_24dp); //Configura ícone

                dialogBebida.setPositiveButton("Ok",null);

                dialogBebida.create();
                dialogBebida.show();
            }
        });

        listViewBebidasPedidos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;

                AlertDialog.Builder dialogBebida = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert progress
                dialogBebida.setTitle("Deseja deletar a bebida selecionada?"); //Configura título e mensagem
                dialogBebida.setCancelable(true); //Configura o cancelamento
                dialogBebida.setIcon(android.R.drawable.ic_delete); //Configura ícone

                dialogBebida.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (tinyDB.containsKey(LISTA_BEBIDAS)){
                            int quantBebidasWithId=0;

                            bebidasPedidas = tinyDB.getListBebidas(LISTA_BEBIDAS);
                            JsonArray itemRemove = tinyDB.getListEstoque(LISTA_ESTOQUE_BEBIDAS);

                            int quantBebidas = Integer.parseInt(bebidasPedidas.get(position).getQuant());//quantidade existente da bebida

                            for (Bebidas bebida:bebidasPedidas) {
                                if (bebida.getId_pedido()>0){//conta quantas bebidas com id do pedido existem
                                    quantBebidasWithId += 1;
                                }
                            }

                            if (bebidasPedidas.get(position).getId_pedido()>0){//ou seja se o item que eu tô deletando tem um id no pedido
                                //então vai ter que adicionar ele na lista de deletar ingrediente
                                JsonObject itensEstoque = new JsonObject();
                                itensEstoque.addProperty("bebida",bebidasPedidas.get(position).getNome());
                                itensEstoque.addProperty("quantidade",-quantBebidas);

                                deletarEstoqueBebidas.add(itensEstoque);
                                tinyDB.putListEstoque(LISTA_ESTOQUE_BEBIDAS_DEL, deletarEstoqueBebidas);

                            } else {
                                itemRemove.remove(position-quantBebidasWithId);
                            }

                            bebidasPedidas.remove(position);

                            tinyDB.putListBebidas(LISTA_BEBIDAS,bebidasPedidas);
                            tinyDB.putListEstoque(LISTA_ESTOQUE_BEBIDAS,itemRemove);

                            if (bebidasPedidas.size()>0){
                                //seta a visibilidade do linear layout e text view (aparece p/ usuário aqui fica tua lista de bebidas)
                                linearList.setVisibility(View.VISIBLE);
                                textSemBebidas.setVisibility(View.GONE);
                            } else {
                                //seta a visibilidade do linear layout e text view (aparece p/ usuário lista de bebidas)
                                linearList.setVisibility(View.GONE);
                                textSemBebidas.setVisibility(View.VISIBLE);
                            }

                            adapter.atualizarLista(bebidasPedidas);
                        }
                    }
                });

                dialogBebida.setNegativeButton("Não",null);

                dialogBebida.create();
                dialogBebida.show();

                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Eventos de clique nos botões do menu

        switch (item.getItemId()){
            case android.R.id.home: //ID do botão gerado automaticamente pelo android
                Intent it = new Intent(this,AdicionarPratoActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                finish();
                break;
            case R.id.action_add_item:
                it = new Intent(this,EscolherBebidaActivity.class);
                startActivity(it);
                break;

            case R.id.action_save_itens:
                JsonArray jsonArray = tinyDB.getListEstoque(LISTA_ESTOQUE_BEBIDAS);
                JsonArray jsonArrayDel = tinyDB.getListEstoque(LISTA_ESTOQUE_BEBIDAS_DEL);

                Log.i("QuantIngs", "Bebidas: "+jsonArray.toString());
                Log.i("QuantIngs", "BebidasDel: "+jsonArrayDel.toString());

                boolean teste = (tinyDB.getListBebidas(LISTA_BEBIDAS).size() > 0) || (tinyDB.getListPratos(LISTA_PRATOS).size() > 0);
                if (teste){
                    it = new Intent(this,FinalizaPedidoActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(it);
                } else {
                    UtilRestaurante.showMensagem(getApplicationContext(),"Você precisa realizar algum pedido!!");
                }
                //verificar se todos os campos estão preenchidos, após salvar os dados e em seguida limpar todos os campos
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
}
