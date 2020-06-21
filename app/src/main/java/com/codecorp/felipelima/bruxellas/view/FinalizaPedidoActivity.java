package com.codecorp.felipelima.bruxellas.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.util.AlterarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.DeletarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.DeletarPedidoAsyncTask;
import com.codecorp.felipelima.bruxellas.util.IncluirAsyncTask;
import com.codecorp.felipelima.bruxellas.util.ListarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.SalvarPedidoAsyncTask;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FinalizaPedidoActivity extends AppCompatActivity {

    private TinyDB tinyDB;
    private RestauranteController controller;
    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String QUANT_PESSOAS = "quant_pessoas";
    private static final String NOME_CLIENTE = "nome_cliente";
    private static final String LISTA_PRATOS = "lista_pratos_pedidos";
    private static final String LISTA_AD_RE_PEDIDO = "lista_ingredientes_AdRe_pedido";
    private static final String LISTA_BEBIDAS = "lista_bebidas_pedidas";
    private static final String OBS_PEDIDO = "obs_pedido";
    private static final String STATUS_PED = "status_pedido";
    private static final String ALTER_IDS_PED = "ids_alterar";
    private static final int NOVO_PED = 0;
    private static final int ALTER_PED = 1;
    private static final String PED_COZ = "cozinha";
    private static final String PED_CX = "caixa";
    private String PREFERENCES = "preferences_user";
    private String REMEMBER_NOME_USER = "user_name_remember";

    private ArrayList<Pratos> pratosPedido;
    private ArrayList<AdicionalRetiradaPedido> adRePedido;
    private ArrayList<Bebidas> bebidasPedido;

    private ArrayList<Pedidos> asPedAlterDel;//é a lista de fato que terão os pedidos que devem ser deletados ao editar o pedido
    private ArrayList<Pedidos> asPedAlter;//é a lista de fato que terão os pedidos que devem ser modificados
    private ArrayList<Pedidos> asPedAlterAdic;//é a lista de fato que terão os pedidos que devem ser inseridos ao editar o pedido

    TextView textNomePrPratosAdRe, textNomePrBebidas,textFMesa,textFCliente,textTotPedido,textStatus;
    EditText editObservacao;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private WifiManager wifi;

    String obsPedido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finaliza_pedido);
        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);

        textNomePrPratosAdRe = findViewById(R.id.textNomePrPratosAdRe);
        textNomePrBebidas = findViewById(R.id.textNomePrBebidas);
        textFMesa = findViewById(R.id.textFMesa1);
        textStatus = findViewById(R.id.textFMesa);
        textFCliente = findViewById(R.id.textfCliente1);
        textTotPedido = findViewById(R.id.textTotPedido);
        editObservacao = findViewById(R.id.textObservacao);

        tinyDB = new TinyDB(getApplicationContext());
        controller = new RestauranteController(getApplicationContext());

        preferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        editor = preferences.edit();

        UtilRestaurante.configuraStatusBar(FinalizaPedidoActivity.this,getSupportActionBar(),"Finalizando pedido");

        String pratos = "";
        Double precoTotPrato = 0.0;

        String bebidas = "";
        Double precoTotBebida = 0.0;

        Double precoTot = 0.0;

        if (tinyDB.containsKey(LISTA_PRATOS) && tinyDB.containsKey(LISTA_AD_RE_PEDIDO)){
            pratosPedido = tinyDB.getListPratos(LISTA_PRATOS);
            adRePedido = tinyDB.getListAdRePedido(LISTA_AD_RE_PEDIDO);
            if (pratosPedido.size()>0) {
                textNomePrPratosAdRe.setVisibility(View.VISIBLE);

                String nomePrecoAd = "";
                String nomeRe = "";
                Double precoAd = 0.0;

                for (int i = 0; i < pratosPedido.size(); i++) {//roda esse for de acordo com o tamanho do array
                    Double prePrato = Double.parseDouble(pratosPedido.get(i).getPreco_venda()) * Double.parseDouble(pratosPedido.get(i).getQuant());
                    String pratoPreto = UtilRestaurante.formatarValorDecimal(prePrato);
                    String nomePrecoPrato = "Qt: " + pratosPedido.get(i).getQuant() + " - " + pratosPedido.get(i).getNome()   + " - R$ " +
                            pratoPreto + condicaoPrato(pratosPedido.get(i).getCond_prato()) +"\n";
                    Double precoPrato = prePrato;

                    nomePrecoAd = "";
                    precoAd = 0.0;
                    if (!adRePedido.get(i).getAdicional().equals("")) {
                        for (String nome : adRePedido.get(i).getAdicional().split(",")) {
                            Ingredientes ing = controller.listarIngredientesNome(nome,0).get(0);
                            Double preAd = Double.parseDouble(ing.getPreco_ad()) * Double.parseDouble(pratosPedido.get(i).getQuant());
                            String adPreco = UtilRestaurante.formatarValorDecimal(preAd);
                            nomePrecoAd = nomePrecoAd + "-> Ad: " + ing.getNome() + " - R$ " + adPreco + "\n";
                            precoAd = precoAd + preAd;
                        }
                    }

                    nomeRe = "";
                    if (!adRePedido.get(i).getRetirada().equals("")) {
                        for (String nome : adRePedido.get(i).getRetirada().split(",")) {
                            //Ingredientes ing = controller.listarIngredientes(Integer.parseInt(nome)).get(0);
                            //nomeRe = nomeRe + "-> Re: " + ing.getNome() + "\n";
                            nomeRe = nomeRe + "-> Re: " + nome + "\n";
                        }
                    }

                    precoTotPrato = precoTotPrato + precoPrato + precoAd;
                    pratos = pratos + nomePrecoPrato + nomePrecoAd + nomeRe + "\n";
                }

                pratos = pratos.substring(0, pratos.length() - 2);

                textNomePrPratosAdRe.setText(pratos);
            } else {
                textNomePrPratosAdRe.setVisibility(View.GONE);
            }
        } else {
            textNomePrPratosAdRe.setVisibility(View.GONE);
            pratosPedido = new ArrayList<>();
            adRePedido = new ArrayList<>();
        }

        if (tinyDB.containsKey(LISTA_BEBIDAS)){
            bebidasPedido = tinyDB.getListBebidas(LISTA_BEBIDAS);

            if (bebidasPedido.size()>0) {
                textNomePrBebidas.setVisibility(View.VISIBLE);
                for (int i = 0; i < bebidasPedido.size(); i++) {
                    Double preBeb = Double.parseDouble(bebidasPedido.get(i).getPreco_venda()) * Double.parseDouble(bebidasPedido.get(i).getQuant());
                    String bebidaPreco = UtilRestaurante.formatarValorDecimal(preBeb);
                    String nomeBebida = "Qt: " + bebidasPedido.get(i).getQuant() + " - " + bebidasPedido.get(i).getNome() + " - R$ " + bebidaPreco + "\n";
                    Double precoBebida = preBeb;

                    precoTotBebida = precoTotBebida + precoBebida;
                    bebidas = bebidas + nomeBebida;
                }
                bebidas = bebidas.substring(0, bebidas.length() - 1);

                textNomePrBebidas.setText(bebidas);
            } else {
                textNomePrBebidas.setVisibility(View.GONE);
            }
        } else {
            textNomePrBebidas.setVisibility(View.GONE);
            bebidasPedido = new ArrayList<>();
        }

        precoTot = precoTotPrato + precoTotBebida;
        String sTotPreto = UtilRestaurante.formatarValorDecimal(precoTot);

        textTotPedido.setText(sTotPreto);

        if (tinyDB.containsKey(MESA_ESCOLHIDA)){
            String mesa = "mesa " + String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA));
            textFMesa.setText(mesa);
        }

        if (tinyDB.containsKey(STATUS_PED)){
            if (tinyDB.getInt(STATUS_PED)>NOVO_PED){
                textStatus.setText("O pedido está sendo alterado para a ");
            } else {
                textStatus.setText("O pedido está sendo realizado para a ");
            }
        }

        if (tinyDB.containsKey(NOME_CLIENTE)){
            String nome = tinyDB.getString(NOME_CLIENTE);
            textFCliente.setText(nome);
        }

        if (tinyDB.containsKey(OBS_PEDIDO)){
            obsPedido = tinyDB.getString(OBS_PEDIDO);
            editObservacao.setText(obsPedido);
        }

        editObservacao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                obsPedido = charSequence.toString();
                tinyDB.putString(OBS_PEDIDO,obsPedido);
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
                Intent it = new Intent(this,AdicionarBebidaActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(it);
                finish();
                break;

            case R.id.action_save_itens2:
                //verifica se o wifi está ao menos conectado
                wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                int ip = info.getIpAddress();

                if (ip == 0) {
                    UtilRestaurante.showMensagem(getApplicationContext(),"Por favor, conecte-se a alguma rede Wi-Fi.");
                    break;
                }

                //Cria progress confirmando se você realmente quer salvar o pedido
                AlertDialog.Builder dialogPedido = new AlertDialog.Builder(this); //Cria uma Alert progress
                final AlertDialog.Builder dialogAddPedido = new AlertDialog.Builder(this);

                if (tinyDB.getInt(STATUS_PED)>NOVO_PED){
                    dialogPedido.setTitle("Deseja realmente alterar esse pedido?"); //Configura título e mensagem
                } else {
                    dialogPedido.setTitle("Deseja realmente salvar esse pedido?"); //Configura título e mensagem
                }

                dialogPedido.setCancelable(false); //Configura o cancelamento
                dialogPedido.setIcon(R.drawable.ic_restaurant_menu_black_24dp); //Configura ícone

                dialogPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int statusPedido = tinyDB.getInt(STATUS_PED);

                        dialogAddPedido.setTitle("Deseja adicionar mais um cliente para esta mesa?"); //Configura título e mensagem
                        dialogAddPedido.setCancelable(false); //Configura o cancelamento
                        dialogAddPedido.setIcon(R.drawable.ic_storage_black_24dp); //Configura ícone

                        dialogAddPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tinyDB.remove(NOME_CLIENTE);
                                tinyDB.remove(LISTA_PRATOS);
                                tinyDB.remove(LISTA_AD_RE_PEDIDO);
                                tinyDB.remove(LISTA_BEBIDAS);
                                tinyDB.remove(OBS_PEDIDO);
                                tinyDB.remove(STATUS_PED);
                                tinyDB.remove(ALTER_IDS_PED);

                                Intent it = new Intent(getApplicationContext(),AdicionarNomeClienteActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(it);
                                finish();
                            }
                        });

                        dialogAddPedido.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tinyDB.remove(MESA_ESCOLHIDA);
                                tinyDB.remove(NOME_CLIENTE);
                                tinyDB.remove(LISTA_PRATOS);
                                tinyDB.remove(LISTA_AD_RE_PEDIDO);
                                tinyDB.remove(LISTA_BEBIDAS);
                                tinyDB.remove(OBS_PEDIDO);
                                tinyDB.remove(STATUS_PED);
                                tinyDB.remove(ALTER_IDS_PED);

                                finish();
                            }
                        });

                        switch (statusPedido){
                            case NOVO_PED://tem que salvar um pedido novo

                                ArrayList<Pedidos> pedido = constroiPedido(pratosPedido, adRePedido, bebidasPedido);

                                SalvarPedidoAsyncTask task = new SalvarPedidoAsyncTask(FinalizaPedidoActivity.this,pedido, false, null, new SalvarPedidoAsyncTask.AsyncResponse() {
                                    @Override
                                    public void processFinish(boolean status) {
                                        if (status) {
                                            UtilRestaurante.showMensagem(getApplicationContext(), "Pedido salvo com sucesso!");

                                            dialogAddPedido.create();
                                            dialogAddPedido.show();
                                        } else {
                                            UtilRestaurante.showMensagem(getApplicationContext(),"Houve algum problema ao salvar o pedido!");
                                        }
                                    }
                                });
                                task.execute();
                                break;

                            case ALTER_PED:
                                if (alteraPedidoBanco()){//tem que deletar os pedidos que vieram a menos
                                    DeletarPedidoAsyncTask deletarTask = new DeletarPedidoAsyncTask(FinalizaPedidoActivity.this, asPedAlterDel, "id-altera", asPedAlter, new DeletarPedidoAsyncTask.AsyncResponse() {
                                        @Override
                                        public void processFinish(boolean status) {
                                            if (status){//tem que deletar os que sobraram
                                                UtilRestaurante.showMensagem(getApplicationContext(),"Pedido alterado com sucesso!");

                                                dialogAddPedido.create();
                                                dialogAddPedido.show();
                                            } else {
                                                UtilRestaurante.showMensagem(getApplicationContext(),"Houve algum problema ao alterar o pedido!");
                                            }
                                        }
                                    });
                                    deletarTask.execute();
                                } else {// tem que inserir os pedidos que vieram a mais
                                    SalvarPedidoAsyncTask salvarTask = new SalvarPedidoAsyncTask(FinalizaPedidoActivity.this, asPedAlterAdic, true, asPedAlter, new SalvarPedidoAsyncTask.AsyncResponse() {
                                        @Override
                                        public void processFinish(boolean status) {
                                            if (status){//tem que deletar os que sobraram
                                                UtilRestaurante.showMensagem(getApplicationContext(),"Pedido alterado com sucesso!");

                                                dialogAddPedido.create();
                                                dialogAddPedido.show();
                                            } else {
                                                UtilRestaurante.showMensagem(getApplicationContext(),"Houve algum problema ao alterar o pedido!");
                                            }
                                        }
                                    });
                                    salvarTask.execute();
                                }
                                break;
                        }
                    }
                });

                dialogPedido.setNegativeButton("Não", null);

                dialogPedido.create();
                dialogPedido.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean alteraPedidoBanco(){
        ArrayList<Integer> listIds = tinyDB.getListInt(ALTER_IDS_PED);
        ArrayList<Pedidos> pedidoAltera = constroiPedido(pratosPedido, adRePedido, bebidasPedido);

        int idSizeAlter = listIds.size();
        int idSizePedidoNovo = pedidoAltera.size();

        if(idSizeAlter >= idSizePedidoNovo){
            //significa que tem que deletar os itens que sobraram da lista de alterar

            asPedAlter = new ArrayList<>();
            for (int i = 0; i < pedidoAltera.size(); i++){//percorre todos os pedidos listados para adicionar o idPk neles
                if (pedidoAltera.get(i).getId()>0) {
                    pedidoAltera.get(i).setIdPK(controller.listarPedidoId(pedidoAltera.get(i).getId()).get(0).getIdPK());
                } else {//está alterando mas significa que é um pedido alterado NOVO
                    pedidoAltera.get(i).setId(substituiID(pedidoAltera,listIds));
                    pedidoAltera.get(i).setIdPK(controller.listarPedidoId(pedidoAltera.get(i).getId()).get(0).getIdPK());
                }
                asPedAlter.add(pedidoAltera.get(i)); //adiciona na lista que será enviado para o async task alterar
            }

            int difAlterPedNew = idSizeAlter - idSizePedidoNovo;//calcula quantos ids tem a mais do que no pedido

            asPedAlterDel = new ArrayList<>();
            if (difAlterPedNew>0) {//se houver pelo menos uma linha de pedido a ser deletada

                ArrayList<Integer> listIdsDel = new ArrayList<>();//lista usada para montar os ids dos pedidos que serão deletados

                //tenho que descobrir quais foram os ids que não foram alterados
                //listIds -> tem todos os ids que estão no pedido que deve ser alterado
                //pratosPedido -> tem os ids que foram alterados

                for (int i = 0; i < idSizeAlter; i++){// roda esse laço a quantidade de pratos que existiam antes de ser alterados
                    boolean test = false;
                    for (Pedidos ped:asPedAlter) {//roda esse laço a quantidade de pratos depois que foram alterados
                        if (listIds.get(i)==ped.getId()){
                            test = true;// seta esse valor se algum prato que tinha antes tiver o mesmo id do de agora, ou seja, se remanesceu algum prato de antes
                        }
                    }
                    if (!test){// se não tiver remanescido nenhum prato
                        listIdsDel.add(listIds.get(i)); // adiciona a lista de pedidos que serão deletados
                    }
                }

                for (int idDel:listIdsDel) {// percorre os ids que serão deletados
                    Pedidos pedDeleta = controller.listarPedidoId(idDel).get(0); // pega o pedidos que deve ser deletado pelo id
                    asPedAlterDel.add(pedDeleta);// adiciona ele ao array de pedidos que serão deletados na async task
                }
            }

            return true;

        } else if (idSizeAlter < idSizePedidoNovo){
            //significa que tem que adicionar os itens que sobraram das listas pratos/bebidas

            asPedAlter = new ArrayList<>();
            for (int i = 0; i < idSizeAlter; i++){//percorre todos os pedidos que possuem ids já existentes para adicionar o id nos mesmos
                if (pedidoAltera.get(i).getId()>0) {
                    pedidoAltera.get(i).setIdPK(controller.listarPedidoId(pedidoAltera.get(i).getId()).get(0).getIdPK());
                } else {//está alterando mas significa que é um pedido alterado NOVO
                    pedidoAltera.get(i).setId(substituiID(pedidoAltera,listIds));
                    pedidoAltera.get(i).setIdPK(controller.listarPedidoId(pedidoAltera.get(i).getId()).get(0).getIdPK());
                }
                asPedAlter.add(pedidoAltera.get(i));
            }

            int sobraPrimIdAdd = pedidoAltera.size() - (pedidoAltera.size() - idSizeAlter); // calcula pra ver se algum pedido tem que alterar

            asPedAlterAdic = new ArrayList<>();
            for (int i = sobraPrimIdAdd; i < pedidoAltera.size(); i++){ // percorre todos os ids que devem ser adicionados no banco, começando pelo primeiro que deve ser adicionado
                pedidoAltera.get(i).setStatus(PED_COZ);
                asPedAlterAdic.add(pedidoAltera.get(i));
            }

            return false;
        }
        return false;
    }

    private int substituiID(ArrayList<Pedidos> pedidoAltera, ArrayList<Integer> listIds) {

        int retorna=0;

        //for (Integer ids:listIds) {
        for (int j = 0; j < listIds.size(); j++) {
            boolean status = false;

            for (int i = 0; i < pedidoAltera.size(); i++) {
                if (pedidoAltera.get(i).getId() == listIds.get(j)){
                    status = true;
                }
            }

            if (!status){
                retorna = listIds.get(j);
                break;
            }

        }

        return retorna;
    }

    private ArrayList<Pedidos> constroiPedido(ArrayList<Pratos> pratosPed, ArrayList<AdicionalRetiradaPedido> adRePed, ArrayList<Bebidas> bebidasPed){
        ArrayList<Pedidos> pedido = new ArrayList<>();

        String mesa = String.valueOf(tinyDB.getInt(MESA_ESCOLHIDA));
        String nome = tinyDB.getString(NOME_CLIENTE);
        String nomeUser = preferences.getString(REMEMBER_NOME_USER,"");

        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM - HH:mm");
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd - HH:mm");
        String dataHora = sdf.format(new Date());

        ArrayList<Pedidos> ped = controller.listarNomesPorMesaPedido(mesa);
        String pessoas;

        if (ped.size()>0){
            if (tinyDB.getInt(STATUS_PED)>NOVO_PED){
                pessoas = tinyDB.getString(QUANT_PESSOAS);
            } else {
                pessoas = "0";
            }
        } else {
            pessoas = tinyDB.getString(QUANT_PESSOAS);
        }

        obsPedido = tinyDB.getString(OBS_PEDIDO);

        if (pratosPed.size() >= bebidasPed.size()){ //Se forem pedidos mais pratos do que bebidas

            for (int i=0; i < pratosPed.size(); i++){
                Pedidos pedidoFinal = new Pedidos();
                //boolean id = true;

                pedidoFinal.setMesa(Integer.parseInt(mesa));
                pedidoFinal.setNome(nome);
                pedidoFinal.setNome_usuario(nomeUser);
                pedidoFinal.setQuant_pessoas(pessoas);///


                pedidoFinal.setPrato(pratosPed.get(i).getNome());
                pedidoFinal.setQuant_prato(pratosPed.get(i).getQuant());///
                pedidoFinal.setCondicao_prato(pratosPed.get(i).getCond_prato());

                if (tinyDB.getInt(STATUS_PED)>NOVO_PED) {//Alterando pedido
                    pedidoFinal.setStatus(pratosPed.get(i).getStatus());
                    //if (pratosPed.get(i).getId_pedido()>0) {
                    //    pedidoFinal.setId(pratosPed.get(i).getId_pedido());
                    //    id = false;
                    //}
                } else {
                    pedidoFinal.setStatus(PED_COZ);
                }
                pedidoFinal.setAdicional(adRePed.get(i).getAdicional());
                pedidoFinal.setRetirar(adRePed.get(i).getRetirada());
                String precoBebida;
                String quantBebida;

                if (i < bebidasPed.size()) {
                    pedidoFinal.setBebida(bebidasPed.get(i).getNome());
                    pedidoFinal.setQuant_bebida(bebidasPed.get(i).getQuant());///
                    precoBebida = bebidasPed.get(i).getPreco_venda();
                    quantBebida = bebidasPed.get(i).getQuant();
                    //if (tinyDB.getInt(STATUS_PED)>NOVO_PED) {//Alterando pedido
                    //    if (bebidasPed.get(i).getId_pedido()>0 && id){
                    //        pedidoFinal.setId(bebidasPed.get(i).getId_pedido());
                    //    }
                    //}
                } else {
                    pedidoFinal.setBebida("");
                    pedidoFinal.setQuant_bebida("");
                    precoBebida = "0.0";
                    quantBebida = "1";
                }

                pedidoFinal.setPreco(calculaPrecoLinhaPedido(pratosPed.get(i).getPreco_venda(),adRePed.get(i).getAdicional(),precoBebida,
                                     pratosPed.get(i).getQuant(),quantBebida));

                pedidoFinal.setObs(obsPedido);
                pedidoFinal.setData_hora(dataHora);

                pedido.add(pedidoFinal);
            }

        } else {//Se forem pedidas mais bebidas do que pratos
            for (int i=0; i < bebidasPed.size(); i++){
                Pedidos pedidoFinal = new Pedidos();
                //boolean id = true;

                pedidoFinal.setMesa(Integer.parseInt(mesa));
                pedidoFinal.setNome(nome);
                pedidoFinal.setNome_usuario(nomeUser);
                pedidoFinal.setQuant_pessoas(pessoas);///

                String precoPrato;
                String precoAd;
                String quantPrato;

                if (i < pratosPed.size()){
                    pedidoFinal.setPrato(pratosPed.get(i).getNome());
                    pedidoFinal.setQuant_prato(pratosPed.get(i).getQuant());///
                    pedidoFinal.setCondicao_prato(pratosPed.get(i).getCond_prato());
                    if (tinyDB.getInt(STATUS_PED)>NOVO_PED) {//Alterando pedido
                        pedidoFinal.setStatus(pratosPed.get(i).getStatus());
                        //if (pratosPed.get(i).getId_pedido()>0) {
                        //    pedidoFinal.setId(pratosPed.get(i).getId_pedido());
                        //    id = false;
                        //}
                    } else {
                        pedidoFinal.setStatus(PED_COZ);
                    }
                    pedidoFinal.setAdicional(adRePed.get(i).getAdicional());
                    pedidoFinal.setRetirar(adRePed.get(i).getRetirada());
                    precoPrato = pratosPed.get(i).getPreco_venda();
                    precoAd = adRePed.get(i).getAdicional();
                    quantPrato = pratosPed.get(i).getQuant();
                } else {
                    pedidoFinal.setPrato("");
                    pedidoFinal.setQuant_prato("");
                    pedidoFinal.setStatus(PED_CX);
                    pedidoFinal.setAdicional("");
                    pedidoFinal.setRetirar("");
                    precoPrato = "0.0";
                    precoAd = "";
                    quantPrato = "1";
                }

                pedidoFinal.setBebida(bebidasPed.get(i).getNome());
                pedidoFinal.setQuant_bebida(bebidasPed.get(i).getQuant());///

                //if (tinyDB.getInt(STATUS_PED)>NOVO_PED) {//Alterando pedido
                //    if (bebidasPed.get(i).getId_pedido()>0 && id){
                //        pedidoFinal.setId(bebidasPed.get(i).getId_pedido());
                //    }
                //}

                pedidoFinal.setPreco(calculaPrecoLinhaPedido(precoPrato,precoAd,bebidasPed.get(i).getPreco_venda(),
                                     quantPrato,bebidasPed.get(i).getQuant()));

                pedidoFinal.setObs(obsPedido);
                pedidoFinal.setData_hora(dataHora);

                pedido.add(pedidoFinal);
            }
        }
        return pedido;
    }

    private String calculaPrecoLinhaPedido(String precoPrato, String idAdicionais, String precoBebida, String qtPrato, String qtBebida){
        double preco = 0.0;
        double precoBeb = 0.0;

        if (!idAdicionais.equals("")){
            for (String nome:idAdicionais.split(",")) {
                //Ingredientes ing = controller.listarIngredientes(Integer.parseInt(nome)).get(0);
                Ingredientes ing = controller.listarIngredientesNome(nome,0).get(0);
                preco = preco + Double.parseDouble(ing.getPreco_ad());
            }
        }
        if (!precoBebida.equals("")){
            precoBeb = Double.parseDouble(precoBebida);
        }

        preco = ((preco + Double.parseDouble(precoPrato)) * Double.parseDouble(qtPrato)) + (precoBeb * Double.parseDouble(qtBebida));

        String sPreco = UtilRestaurante.formatarValorDecimal(preco);

        return sPreco;
    }

    private String condicaoPrato(int cond){
        String condicao="";
        switch (cond){
            case 1:
                condicao = " - Dps";
                break;
            case 2:
                condicao = " - Vgm";
                break;
            case 3:
                condicao = " - Dps/Vgm";
                break;
        }
        return condicao;
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(getApplicationContext(),AdicionarBebidaActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(it);
        super.onBackPressed();
    }

}