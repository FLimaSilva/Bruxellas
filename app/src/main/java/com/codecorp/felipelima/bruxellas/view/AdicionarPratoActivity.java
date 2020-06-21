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

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.PedidoPratosAdReAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
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


public class AdicionarPratoActivity extends AppCompatActivity{

    private TinyDB tinyDB;
    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String NOME_CLIENTE = "nome_cliente";
    private static final String LISTA_PRATOS = "lista_pratos_pedidos";
    private static final String LISTA_AD_RE_PEDIDO = "lista_ingredientes_AdRe_pedido";
    private static final String LISTA_ESTOQUE = "lista_estoque_pedido";
    private static final String LISTA_ESTOQUE_DEL = "lista_estoque_pedido_deleta";
    private static final String STATUS_PED = "status_pedido";
    private static final int NOVO_PED = 0;

    private ArrayList<Pratos> pratosPedidos;
    private ArrayList<AdicionalRetiradaPedido> ADREPed;
    private RestauranteController controller;
    private JsonArray deletarEstoquePratos;

    private TextView textMesa,textCliente,textSemPratos,textStatus;
    private ListView listViewPratosPedidos;
    private LinearLayout linearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_prato);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        UtilRestaurante.configuraStatusBar(AdicionarPratoActivity.this,getSupportActionBar(),"Escolha os pratos");

        textStatus = findViewById(R.id.textPMesa);
        textMesa = findViewById(R.id.textPMesa1);
        textCliente = findViewById(R.id.textPCliente1);
        textSemPratos = findViewById(R.id.textSemPratos);
        linearList = findViewById(R.id.linearListPratos);
        listViewPratosPedidos = findViewById(R.id.listViewPratosPedidos);

        tinyDB = new TinyDB(getApplicationContext());
        String mesa = "mesa " + String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA));
        String cliente = tinyDB.getString(NOME_CLIENTE);

        textMesa.setText(mesa);
        textCliente.setText(cliente);

        if (tinyDB.containsKey(LISTA_PRATOS) && tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
            pratosPedidos = tinyDB.getListPratos(LISTA_PRATOS);
            ADREPed = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);
            if (pratosPedidos.size()>0 && ADREPed.size()>0){
                linearList.setVisibility(View.VISIBLE);
                textSemPratos.setVisibility(View.GONE);
            }
        } else {
            pratosPedidos = new ArrayList<>();
            ADREPed = new ArrayList<>();
            linearList.setVisibility(View.GONE);
            textSemPratos.setVisibility(View.VISIBLE);
        }

        if (tinyDB.containsKey(STATUS_PED)){
            if (tinyDB.getInt(STATUS_PED)>NOVO_PED){
                textStatus.setText("O pedido está sendo alterado para a ");
            } else {
                textStatus.setText("O pedido está sendo realizado para a ");
            }
        }

        if (tinyDB.containsKey(LISTA_ESTOQUE_DEL)){
            deletarEstoquePratos = tinyDB.getListEstoque(LISTA_ESTOQUE_DEL);
        } else {
            deletarEstoquePratos = new JsonArray();
        }

        final PedidoPratosAdReAdapter adapter = new PedidoPratosAdReAdapter(getApplicationContext(),pratosPedidos,ADREPed);
        listViewPratosPedidos.setAdapter(adapter);

        controller = new RestauranteController(getApplicationContext());

        listViewPratosPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                    pratosPedidos = tinyDB.getListPratos(LISTA_PRATOS);
                    ADREPed = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);

                    String sPrecoPratos = UtilRestaurante.formatarValorDecimal(pratosPedidos.get(i).getPreco_venda());
                    String textDialog = pratosPedidos.get(i).getNome() + " - R$ " + sPrecoPratos +"\n";

                    if (!ADREPed.get(i).getAdicional().equals("")) {
                        String separaAd[] = ADREPed.get(i).getAdicional().split(",");

                        for (String nome : separaAd) {
                            //Ingredientes ing_id = controller.listarIngredientes(Integer.parseInt(id)).get(0);
                            Ingredientes ing_id = controller.listarIngredientesNome(nome,0).get(0);
                            String sPrecoAd = UtilRestaurante.formatarValorDecimal(ing_id.getPreco_ad());
                            textDialog = textDialog + "Ad: " + ing_id.getNome() + " - R$ " + sPrecoAd + "\n";
                        }
                    }

                    if (!ADREPed.get(i).getRetirada().equals("")) {
                        String separaRe[] = ADREPed.get(i).getRetirada().split(",");

                        for (String nome : separaRe) {
                            //Ingredientes ing_id = controller.listarIngredientes(Integer.parseInt(nome)).get(0);
                            Ingredientes ing_id = controller.listarIngredientesNome(nome,0).get(0);
                            textDialog = textDialog + "Re: " + ing_id.getNome() + "\n";
                        }
                    }

                    AlertDialog.Builder dialogPrato = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog
                    dialogPrato.setTitle("Prato escolhido"); //Configura título e mensagem
                    dialogPrato.setMessage(textDialog);
                    dialogPrato.setCancelable(true); //Configura o cancelamento
                    dialogPrato.setIcon(R.drawable.ic_restaurant_menu_black_24dp); //Configura ícone

                    dialogPrato.setPositiveButton("Ok", null);

                    dialogPrato.create();
                    dialogPrato.show();
                }
        });

        listViewPratosPedidos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int position = i;

                AlertDialog.Builder dialogPrato = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog
                dialogPrato.setTitle("Deseja deletar o prato selecionado?"); //Configura título e mensagem
                dialogPrato.setCancelable(true); //Configura o cancelamento
                dialogPrato.setIcon(android.R.drawable.ic_delete); //Configura ícone

                dialogPrato.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (tinyDB.containsKey(LISTA_PRATOS) && tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
                            int quantPratosWithId=0;

                            pratosPedidos = tinyDB.getListPratos(LISTA_PRATOS);
                            ADREPed = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);

                            JsonArray itemRemove = tinyDB.getListEstoque(LISTA_ESTOQUE);

                            int quantPratos = Integer.parseInt(pratosPedidos.get(position).getQuant());//quantidade existente do prato

                            for (Pratos prato:pratosPedidos) {
                                if (prato.getId_pedido()>0){//conta quantos pratos com id do pedido existem
                                    quantPratosWithId += 1;
                                }
                            }

                            if (pratosPedidos.get(position).getId_pedido()>0){//ou seja se o item que eu tô deletando tem um id no pedido
                                //então vai ter que adicionar ele na lista de deletar ingrediente
                                JsonObject itensEstoque = new JsonObject();
                                itensEstoque.addProperty("prato",pratosPedidos.get(position).getNome());
                                itensEstoque.addProperty("quantidade",-quantPratos);

                                if (!ADREPed.get(position).getAdicional().equals("")) {
                                    itensEstoque.addProperty("adicional", ADREPed.get(position).getAdicional());
                                    //itensEstoque.addProperty("quantidade", -quantPratos);
                                }

                                if (!ADREPed.get(position).getRetirada().equals("")) {
                                    itensEstoque.addProperty("retirada", ADREPed.get(position).getRetirada());
                                    itensEstoque.addProperty("quantidadeRe", quantPratos);
                                }

                                deletarEstoquePratos.add(itensEstoque);
                                tinyDB.putListEstoque(LISTA_ESTOQUE_DEL, deletarEstoquePratos);

                            } else {
                                itemRemove.remove(position-quantPratosWithId);
                            }

                            pratosPedidos.remove(position);
                            ADREPed.remove(position);

                            tinyDB.putListPratos(LISTA_PRATOS,pratosPedidos);
                            tinyDB.putListAdRePedido(LISTA_AD_RE_PEDIDO,ADREPed);
                            tinyDB.putListEstoque(LISTA_ESTOQUE,itemRemove);

                            if (pratosPedidos.size()>0 && ADREPed.size()>0){
                                linearList.setVisibility(View.VISIBLE);
                                textSemPratos.setVisibility(View.GONE);
                            } else {
                                linearList.setVisibility(View.GONE);
                                textSemPratos.setVisibility(View.VISIBLE);
                            }

                            PedidoPratosAdReAdapter adapter2 = new PedidoPratosAdReAdapter(getApplicationContext(),pratosPedidos,ADREPed);
                            listViewPratosPedidos.setAdapter(adapter2);
                        }
                    }
                });

                dialogPrato.setNegativeButton("Não", null);

                dialogPrato.create();
                dialogPrato.show();

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
                Intent it = new Intent(this,AdicionarNomeClienteActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                finish();
                break;
            case R.id.action_add_item:
                it = new Intent(this,EscolherPratoActivity.class);
                startActivity(it);
                break;

            case R.id.action_save_itens:
                JsonArray jsonArray = tinyDB.getListEstoque(LISTA_ESTOQUE);
                JsonArray jsonArrayDel = tinyDB.getListEstoque(LISTA_ESTOQUE_DEL);
                Log.i("QuantIngs", "PratosDel: "+jsonArrayDel.toString());
                Log.i("QuantIngs", "Pratos: "+jsonArray.toString());

                it = new Intent(this,AdicionarBebidaActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                //verificar se todos os campos estão preenchidos, após salvar os dados e em seguida limpar todos os campos
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(getApplicationContext(),AdicionarNomeClienteActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
        super.onBackPressed();
    }

}
