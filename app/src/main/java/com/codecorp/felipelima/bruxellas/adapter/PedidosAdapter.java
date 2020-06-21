package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.codecorp.felipelima.bruxellas.view.AdicionarNomeClienteActivity;

import java.util.ArrayList;

public class PedidosAdapter extends ArrayAdapter<Pedidos> implements View.OnClickListener{

    ViewHolder linha;
    private ArrayList<Pedidos> pedidosAtivos;
    private RestauranteController controller;
    private ArrayList<Pedidos> pedido;
    private Pratos pratos = new Pratos();
    private Ingredientes ingredientes = new Ingredientes();
    private Bebidas bebidas = new Bebidas();

    private int count=0;
    private boolean sucesso=true;
    private ArrayList<Pedidos> pedStatus;

    String nomeDel="Todos";
    String nomeEdt="";
    String nomeSave="Todos";

    private TinyDB tinyDB;
    private static final String MESA_ESCOLHIDA = "mesa_escolhida";
    private static final String NOME_CLIENTE = "nome_cliente";
    private static final String QUANT_PESSOAS = "quant_pessoas";
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
    private static final String PED_PRONTO = "pronto";

    private  ArrayList<Pedidos> pedidosEditar;
    private  ArrayList<Pratos> pratosPedidos;
    private  ArrayList<AdicionalRetiradaPedido> adicionalRetiradaPedidos;
    private  ArrayList<Bebidas> bebidasPedido;

    private final Context ctx;

    private static class ViewHolder {

        TextView txtMesa;
        TextView txtClientes;
        TextView txtPrecoTotal;
        TextView txtStatusPed;
        ImageView imgMesa;
        ImageView imgConsultar;
        ImageView imgDeletar;
        ImageView imgEditar;
        ImageView imgSalvar;

        Pedidos pedidos;

    }

    public PedidosAdapter(ArrayList<Pedidos> pedido, @NonNull Context context) {
        super(context, R.layout.list_view_pedidos_new, pedido);

        this.pedidosAtivos = pedido;

        tinyDB = new TinyDB(context);

        ctx = context;
    }

    public void atualizarLista(ArrayList<Pedidos> novasPedidos) {
        this.pedidosAtivos.clear();
        this.pedidosAtivos.addAll(novasPedidos);

        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        ViewHolder linha = (ViewHolder) view.getTag();
        controller = new RestauranteController(getContext());

        String nomes[] = linha.pedidos.getNome().split(",");
        ArrayList<String> arrayNome = new ArrayList<>();

        switch (view.getId()){
            case R.id.imgConsultar:
                arrayNome.clear();

                for (String names:nomes) {
                    arrayNome.add(names);
                }

                final String listagemPedido = resumePedido(arrayNome,String.valueOf(linha.pedidos.getMesa()));

                AlertDialog.Builder dialogListaPedido = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog

                dialogListaPedido.setTitle("Pedido da mesa " + linha.pedidos.getMesa()); //Configura título e mensagem
                dialogListaPedido.setMessage(listagemPedido);
                dialogListaPedido.setCancelable(true); //Configura o cancelamento
                dialogListaPedido.setIcon(R.drawable.ic_restaurant_menu_black_24dp); //Configura ícone

                dialogListaPedido.setPositiveButton("Ok",null);

                dialogListaPedido.create();
                dialogListaPedido.show();

                break;

            case R.id.imgDeletar:

                nomeDel = "Todos";

                String opcoesDel = "Todos, "+linha.pedidos.getNome();
                final String opcoesDelSep[] = opcoesDel.split(", ");
                final String mesaDel = String.valueOf(linha.pedidos.getMesa());


                final AlertDialog.Builder dialogDelPedido = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog

                dialogDelPedido.setTitle("Selecione o pedido da mesa " + linha.pedidos.getMesa() + " para deletar"); //Configura título e mensagem
                dialogDelPedido.setCancelable(true); //Configura o cancelamento
                dialogDelPedido.setIcon(android.R.drawable.ic_delete); //Configura ícone
                dialogDelPedido.setSingleChoiceItems(opcoesDelSep, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nomeDel = opcoesDelSep[i];
                    }
                });

                dialogDelPedido.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //aqui tem que deletar o que foi escolhido pelo usuário

                        if (nomeDel=="Todos") {//deleta todos os pedidos da mesa
                            ArrayList<Pedidos> pedDeletarIdPk = controller.listarNomesPorMesaPedidoAll(mesaDel);

                            DeletarPedidoAsyncTask deletarTask = new DeletarPedidoAsyncTask(getContext(), pedDeletarIdPk, "todos",null, new DeletarPedidoAsyncTask.AsyncResponse() {
                                @Override
                                public void processFinish(boolean status) {
                                    if (status){
                                        UtilRestaurante.showMensagem(getContext(),"O pedido da mesa "+ mesaDel +" foi deletado com sucesso!!");
                                    } else {
                                        UtilRestaurante.showMensagem(getContext(),"Houve algum problema para deletar o pedido da "+ mesaDel +".");
                                    }
                                }
                            });
                            deletarTask.execute();

                        } else {//deleta apenas o pedido que foi passado o nome
                            ArrayList<Pedidos> pedDel = controller.listarPedidoPorNomeMesa(mesaDel,nomeDel);

                            DeletarPedidoAsyncTask deletarTask = new DeletarPedidoAsyncTask(getContext(), pedDel, "nome", null, new DeletarPedidoAsyncTask.AsyncResponse() {
                                @Override
                                public void processFinish(boolean status) {
                                    if (status){
                                        UtilRestaurante.showMensagem(getContext(),"O pedido da mesa "+ mesaDel +" do(a) cliente " + nomeDel + " foi deletado com sucesso!!");
                                    } else {
                                        UtilRestaurante.showMensagem(getContext(),"Houve algum problema para deletar o pedido da mesa "+ mesaDel +"  do(a) cliente: " + nomeDel +".");
                                    }
                                }
                            });
                            deletarTask.execute();
                        }

                        try{
                            Thread.sleep(200);
                        } catch (Exception e)
                        {
                            Log.e("Exception e", "Message: "+e.getMessage());
                        }

                        atualizarLista(atualizaDados()); //atualiza listview com os dados consultados do banco
                    }
                });

                dialogDelPedido.setNegativeButton("Cancelar",null);

                dialogDelPedido.create();
                dialogDelPedido.show();

                break;

            case R.id.imgEditar:

                // preciso carregar todos os dados do pedido filtrados por nome e mesa no TinyDB e e encaminhar para a tela do adicionar
                // adicionar nome do cliente e deixar ele efetuar tod0 o processo de alteracao (arrumar uma maneira de utilizar o
                // UPDATE do banco de dados na mesma tela em que ocorre o INSERT)!!!!

                final String opcoesEdtSep[] = linha.pedidos.getNome().split(", ");
                final String mesaEdt = String.valueOf(linha.pedidos.getMesa());

                nomeEdt = opcoesEdtSep[0];

                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//obtém o layout
                View dialogView = inflater.inflate(R.layout.number_picker_dialog,null); //pega o layout e infla nele a tela do numberpicker transformando tudo em uma view

                final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);

                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(20);
                numberPicker.setWrapSelectorWheel(false);

                final AlertDialog.Builder dialogNumPessoas = new AlertDialog.Builder(view.getRootView().getContext());

                dialogNumPessoas.setTitle("Pessoas na mesa");
                dialogNumPessoas.setIcon(R.drawable.ic_restaurant_menu_black_24dp);
                dialogNumPessoas.setMessage("Selecione a quantidade de pessoas na mesa");
                dialogNumPessoas.setCancelable(false);
                dialogNumPessoas.setView(dialogView);

                AlertDialog.Builder dialogEdtPedido = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog

                dialogEdtPedido.setTitle("Selecione o pedido da mesa " + linha.pedidos.getMesa() + " para editar"); //Configura título e mensagem
                dialogEdtPedido.setCancelable(true); //Configura o cancelamento
                dialogEdtPedido.setIcon(R.drawable.ic_storage_black_24dp); //Configura ícone
                dialogEdtPedido.setSingleChoiceItems(opcoesEdtSep, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nomeEdt = opcoesEdtSep[i];
                    }
                });

                dialogEdtPedido.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // aqui deve ser selecionado o nome escolhido
                        pedidosEditar = controller.listarPedidoPorNomeMesaOrdId(mesaEdt,nomeEdt); //Lista todos os pedidos da pessoa selecionada
                        String quant_pessoas = pedidosEditar.get(0).getQuant_pessoas();

                        pratosPedidos = new ArrayList<>(); //cria a estrutura que guardara os pratos pedidos
                        adicionalRetiradaPedidos = new ArrayList<>(); //cria a estrutura que guardara os adicionais/retiradas pedidas
                        bebidasPedido = new ArrayList<>(); //cria a estrutura que guardara as bebidas pedidas
                        ArrayList<Integer> ids = new ArrayList<>();

                        for (Pedidos pedEdt:pedidosEditar) {//roda esse laço para quantos pedidos existirem para a pessoa selecionada

                            if (!pedEdt.getPrato().equals("")){//verifica se existe algum prato nessa linha do pedido
                                Pratos prat = controller.listarPratosNome(pedEdt.getPrato(),1).get(0);
                                prat.setId_pedido(pedEdt.getId());
                                prat.setQuant(pedEdt.getQuant_prato());
                                prat.setCond_prato(pedEdt.getCondicao_prato());
                                prat.setStatus(pedEdt.getStatus());
                                pratosPedidos.add(prat);

                                AdicionalRetiradaPedido adRePed = new AdicionalRetiradaPedido();//cria estrutura onde ficará os adicionais e retiradas do pedido

                                if (!pedEdt.getAdicional().equals("")){//verifica se para o prato adicionado existe algum adicional
                                    adRePed.setAdicional(pedEdt.getAdicional());
                                } else {
                                    adRePed.setAdicional("");
                                }

                                if (!pedEdt.getRetirar().equals("")){//verifica se para o prato adicionado existe alguma retirada
                                    adRePed.setRetirada(pedEdt.getRetirar());
                                } else {
                                    adRePed.setRetirada("");
                                }

                                adicionalRetiradaPedidos.add(adRePed);
                            }

                            if (!pedEdt.getBebida().equals("")){//verifica se existe alguma pedida nessa linha do pedido
                                Bebidas bebid = controller.listarBebidasNome(pedEdt.getBebida()).get(0);
                                bebid.setId_pedido(pedEdt.getId());
                                bebid.setQuant(pedEdt.getQuant_bebida());
                                bebidasPedido.add(bebid);
                            }
                            ids.add(pedEdt.getId());//tem que ser organizado pelo ID, pois agora está vindo bagunçado
                        }

                        tinyDB.putInt(MESA_ESCOLHIDA,Integer.valueOf(mesaEdt));//insere a mesa do pedido
                        tinyDB.putString(NOME_CLIENTE,nomeEdt);//insere o nome do cliente do pedido
                        tinyDB.putListPratos(LISTA_PRATOS,pratosPedidos);//insere todos os pratos do pedido
                        tinyDB.putListAdRePedido(LISTA_AD_RE_PEDIDO, adicionalRetiradaPedidos); // insere todos os AdRe dos pratos do pedido
                        tinyDB.putListBebidas(LISTA_BEBIDAS,bebidasPedido); //insere todas as bebidas do pedido
                        tinyDB.putString(OBS_PEDIDO,pedidosEditar.get(0).getObs());//insere a observação do pedido(primeira linha --- "tanto faz")
                        tinyDB.putInt(STATUS_PED,ALTER_PED);//passa informação de se o pedido deve ser adicionado ou modificado
                        tinyDB.putListInt(ALTER_IDS_PED,ids); //passa os ids que devem ser alterados no pedido
                        tinyDB.putString(QUANT_PESSOAS,quant_pessoas);

                        dialogNumPessoas.setPositiveButton("Escolher", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tinyDB.putString(QUANT_PESSOAS,String.valueOf(numberPicker.getValue()));

                                Intent it = new Intent(getContext(), AdicionarNomeClienteActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                getContext().startActivity(it); //chama uma outra activity
                            }
                        });
                        dialogNumPessoas.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        if (Integer.parseInt(pedidosEditar.get(0).getQuant_pessoas())>0){//significa que tem que alterar a quantidade de pessoas
                            numberPicker.setValue(Integer.parseInt(pedidosEditar.get(0).getQuant_pessoas()));

                            dialogNumPessoas.create();
                            dialogNumPessoas.show();
                        } else {
                            Intent it = new Intent(getContext(), AdicionarNomeClienteActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            getContext().startActivity(it); //chama uma outra activity
                        }
                    }
                });

                dialogEdtPedido.setNegativeButton("Cancelar",null);

                dialogEdtPedido.create();
                dialogEdtPedido.show();

                break;

            case R.id.imgSalvar:

                nomeSave = "Todos";

                String opcoesSave = "Todos, "+linha.pedidos.getNome();
                final String apenasNomes = linha.pedidos.getNome();
                final String opcoesSaveSep[] = opcoesSave.split(", ");
                final String mesaSave = String.valueOf(linha.pedidos.getMesa());

                AlertDialog.Builder dialogSavePedido = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog
                final AlertDialog dialogResPedido = new AlertDialog.Builder(view.getRootView().getContext()).create(); //Cria uma Alert dialog

                dialogSavePedido.setTitle("Selecione o pedido da mesa " + linha.pedidos.getMesa() + " para fechar"); //Configura título e mensagem
                dialogSavePedido.setCancelable(true); //Configura o cancelamento
                dialogSavePedido.setIcon(R.drawable.ic_restaurant_menu_black_24dp); //Configura ícone
                dialogSavePedido.setSingleChoiceItems(opcoesSaveSep, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nomeSave = opcoesSaveSep[i];
                    }
                });

                dialogSavePedido.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<String> nomes = new ArrayList<>();
                        nomes.clear();

                        double valorTot = 0.0;
                        double valorTx = 0.0;
                        double valorCompleto = 0.0;

                        if (nomeSave.equals("Todos")){
                            String name[] = apenasNomes.split(", ");
                            for (String names:name) {
                                nomes.add(names);
                            }
                            ArrayList<Pedidos> pedTot = controller.listarMesasPedido();//recupera a quantidade de mesas abertas
                            for (Pedidos ped:pedTot) {
                                if (String.valueOf(ped.getMesa()).equals(mesaSave)){
                                    valorTot = Double.parseDouble(ped.getPreco());
                                }
                            }

                        } else {
                            nomes.add(nomeSave);
                            ArrayList<Pedidos> pedTot = controller.listarNomesPorMesaPedido(mesaSave);//recupera a quantidade de mesas abertas
                            for (Pedidos ped:pedTot) {
                                if (ped.getNome().equals(nomeSave)){
                                    valorTot = Double.parseDouble(ped.getPreco());
                                }
                            }
                        }
                        String listaAllPedido = resumePedido(nomes, mesaSave);

                        String precoTotal = UtilRestaurante.formatarValorDecimal(valorTot);
                        listaAllPedido = listaAllPedido + "\n\n" + "O valor total do pedido é: R$ " + precoTotal + "\n\n";

                        valorTx = (valorTot * 0.1); // 10% de taxa
                        String precoTx = UtilRestaurante.formatarValorDecimal(valorTx);
                        listaAllPedido = listaAllPedido + "O valor da taxa de serviço é: R$ " + precoTx + "\n\n";

                        valorCompleto = valorTot + valorTx;
                        String precoCompleto = UtilRestaurante.formatarValorDecimal(valorCompleto);
                        listaAllPedido = listaAllPedido + "O valor total é: R$ " + precoCompleto;

                        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogView = inflater.inflate(R.layout.taxa_servico,null); //pega o layout e infla nele a tela do numberpicker transformando tudo em uma view

                        final CheckBox cbTxServico = dialogView.findViewById(R.id.txServico);
                        cbTxServico.setChecked(true);

                        //dialogResPedido.setView(dialogView);
                        dialogResPedido.setTitle("Resumo do pedido"); //Configura título e mensagem
                        dialogResPedido.setCancelable(true); //Configura o cancelamento
                        dialogResPedido.setIcon(R.drawable.ic_storage_black_24dp); //Configura ícone
                        dialogResPedido.setMessage(listaAllPedido);

                        dialogResPedido.setButton(AlertDialog.BUTTON_POSITIVE,"Com taxa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (nomeSave=="Todos") {//deleta todos os pedidos da mesa
                                    if (controller.deletarPedidoMesa(mesaSave)){
                                        UtilRestaurante.showMensagem(getContext(),"O pedido da "+ mesaSave +" foi fechado com sucesso!!");
                                    } else {
                                        UtilRestaurante.showMensagem(getContext(),"Houve algum problema para salvar o pedido da "+ mesaSave +".");
                                    }

                                } else {//deleta apenas o pedido que foi passado o nome
                                    if (controller.deletarPedidoNome(mesaSave,nomeSave)){
                                        UtilRestaurante.showMensagem(getContext(),"O pedido da "+ mesaSave +" do(a) cliente " + nomeSave + " foi fechado com sucesso!!");
                                    } else {
                                        UtilRestaurante.showMensagem(getContext(),"Houve algum problema para salvar o pedido da "+ mesaSave +"  do(a) cliente: " + nomeDel +".");
                                    }
                                }

                                atualizarLista(atualizaDados()); //atualiza listview com os dados consultados do banco
                            }
                        });

                        dialogResPedido.setButton(AlertDialog.BUTTON_NEGATIVE, "Sem taxa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (nomeSave=="Todos") {//deleta todos os pedidos da mesa
                                    if (controller.deletarPedidoMesa(mesaSave)){
                                        UtilRestaurante.showMensagem(getContext(),"O pedido da "+ mesaSave +" foi fechado com sucesso!!");
                                    } else {
                                        UtilRestaurante.showMensagem(getContext(),"Houve algum problema para salvar o pedido da "+ mesaSave +".");
                                    }

                                } else {//deleta apenas o pedido que foi passado o nome
                                    if (controller.deletarPedidoNome(mesaSave,nomeSave)){
                                        UtilRestaurante.showMensagem(getContext(),"O pedido da "+ mesaSave +" do(a) cliente " + nomeSave + " foi fechado com sucesso!!");
                                    } else {
                                        UtilRestaurante.showMensagem(getContext(),"Houve algum problema para salvar o pedido da "+ mesaSave +"  do(a) cliente: " + nomeDel +".");
                                    }
                                }

                                atualizarLista(atualizaDados()); //atualiza listview com os dados consultados do banco
                            }
                        });

                        dialogResPedido.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        //dialogResPedido.create();
                        dialogResPedido.show();//.getButton(1).setLayoutParams(lp);//.getWindow().setLayout(600,500);

                        Button positiveButton = dialogResPedido.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negativeButton = dialogResPedido.getButton(AlertDialog.BUTTON_NEGATIVE);
                        Button neutralButton = dialogResPedido.getButton(AlertDialog.BUTTON_NEUTRAL);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f);
                        positiveButton.setLayoutParams(lp);
                        negativeButton.setLayoutParams(lp);
                        neutralButton.setLayoutParams(lp);
                    }
                });

                dialogSavePedido.setNegativeButton("Cancelar",null);

                dialogSavePedido.create();
                dialogSavePedido.show();
                break;

            case R.id.imgMesa:
                if (linha.pedidos.getStatus().equals(PED_PRONTO)){
                    AlertDialog.Builder dialogPedidoEntregue = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog

                    final String mesaStatus = String.valueOf(linha.pedidos.getMesa());
                    final String mesaNome = "mesa " + linha.pedidos.getMesa();

                    dialogPedidoEntregue.setTitle("Pedido da " + mesaNome); //Configura título e mensagem
                    dialogPedidoEntregue.setMessage("O pedido desta mesa já foi totalmente entregue?");
                    dialogPedidoEntregue.setCancelable(true); //Configura o cancelamento
                    dialogPedidoEntregue.setIcon(R.drawable.ic_restaurant_menu_black_24dp); //Configura ícone

                    dialogPedidoEntregue.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean sucess = true;
                            sucesso = true;
                            count = 0;

                            pedStatus = controller.listarNomesPorMesaPedidoAll(mesaStatus,PED_PRONTO);

                            for (Pedidos ped:pedStatus) {
                                try {
                                    ped.setStatus(PED_CX);
                                    sucess = sucess && controller.alterarPedidoCoz(ped);

                                    if (sucess) {
                                        AlterarAsyncTask task = new AlterarAsyncTask(ped, getContext(), true, new AlterarAsyncTask.AsyncResponse() {
                                            @Override
                                            public void processFinish(boolean output) {
                                                verificaQuantPratos(output);
                                            }
                                        });
                                        task.execute();
                                    }
                                } catch (Exception e){
                                    Log.e("Servidor","Falha ao salvar dados de prato entregue: "+e.getMessage());
                                }
                            }
                        }
                    });

                    dialogPedidoEntregue.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    dialogPedidoEntregue.create();
                    dialogPedidoEntregue.show();
                }

                break;
        }

    }

    private void verificaQuantPratos(boolean sucess) {
        count = count + 1;
        sucesso = sucesso && sucess;
        if (count >= pedStatus.size()) {
            if (sucesso) {
                UtilRestaurante.showMensagem(getContext(),
                        "O(s) prato(s) da mesa " + pedStatus.get(0).getMesa() + " foram entregues!");
                atualizarLista(atualizaDados());

            } else {
                UtilRestaurante.showMensagem(getContext(),
                        "Ocorreu algum problema ao salvar a entrega dos pratos!");
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position,
                        View dataSet,
                        @NonNull ViewGroup parent) {

        if (dataSet == null) {
            linha = new ViewHolder();

            LayoutInflater mesasAbertas = LayoutInflater.from(getContext());

            dataSet = mesasAbertas.inflate(R.layout.list_view_pedidos_new,
                    parent,
                    false);

            linha.txtMesa = dataSet.findViewById(R.id.txtMesa);
            linha.txtClientes = dataSet.findViewById(R.id.txtClientes);
            linha.txtPrecoTotal = dataSet.findViewById(R.id.txtPrecoTotal);
            linha.txtStatusPed = dataSet.findViewById(R.id.txtStatusPed);
            linha.imgMesa = dataSet.findViewById(R.id.imgMesa);
            linha.imgConsultar = dataSet.findViewById(R.id.imgConsultar);
            linha.imgDeletar = dataSet.findViewById(R.id.imgDeletar);
            linha.imgEditar = dataSet.findViewById(R.id.imgEditar);
            linha.imgSalvar = dataSet.findViewById(R.id.imgSalvar);

            dataSet.setTag(linha);
        }else {
            linha = (ViewHolder) dataSet.getTag();
        }

        //linha.imgMesa.setImageResource(R.drawable.icons_mesa_cheia);
        //linha.imgMesa.setImageResource(R.drawable.mesa_sw);
        linha.imgMesa.setImageResource(R.drawable.mesa_sw_png);
        linha.imgSalvar.setImageResource(android.R.drawable.ic_menu_save);

        linha.pedidos = getItem(position);

        String status="";

        switch (linha.pedidos.getStatus()){
            case PED_COZ:
                status = "Na cozinha";
                linha.txtStatusPed.setTextColor(Color.rgb(204,0,0)); //vermelho dark
                break;

            case PED_PRONTO:
                status = "Pronto(s)";
                linha.txtStatusPed.setTextColor(Color.rgb(239,164,45));//amarelo dark
                break;

            case PED_CX:
                status = "Entregue(s)";
                linha.txtStatusPed.setTextColor(Color.rgb(102,153,0));//verde escuro
                break;
        }

        String mesa = "Mesa " + linha.pedidos.getMesa();
        linha.txtMesa.setText(mesa);

        linha.txtClientes.setText(linha.pedidos.getNome());
        linha.txtStatusPed.setText(status);

        String precoTotMesa = "R$ " + UtilRestaurante.formatarValorDecimal(linha.pedidos.getPreco());
        linha.txtPrecoTotal.setText(precoTotMesa);

        linha.imgMesa.setOnClickListener(this);
        linha.imgMesa.setTag(linha);
        linha.imgConsultar.setOnClickListener(this);
        linha.imgConsultar.setTag(linha);
        linha.imgDeletar.setOnClickListener(this);
        linha.imgDeletar.setTag(linha);
        linha.imgEditar.setOnClickListener(this);
        linha.imgEditar.setTag(linha);

        linha.imgSalvar.setVisibility(View.GONE);
        //linha.imgSalvar.setOnClickListener(this);
        //linha.imgSalvar.setTag(linha);

        return dataSet;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public ArrayList<Pedidos> atualizaDados(){

        ArrayList<Pedidos> pedidoTemp = controller.listarMesasPedido();
        ArrayList<Pedidos> pedidoReturn = new ArrayList<>();
        ArrayList<Pedidos> pedNome = new ArrayList<>();
        ArrayList<Pedidos> pedStatus = new ArrayList<>();
        String nomes="";
        String status;

        for (Pedidos ped:pedidoTemp) {

            nomes="";
            status="";
            pedNome.clear();
            pedStatus.clear();

            pedNome.addAll(controller.getNomesPorMesaPedido(String.valueOf(ped.getMesa())));

            for (Pedidos nome:pedNome) {
                nomes = nomes + nome.getNome() + ", ";
            }

            pedStatus.addAll(controller.listarNomesPorMesaPedidoAll(String.valueOf(ped.getMesa()),PED_COZ));

            if (pedStatus.size()>0){
                status = PED_COZ;
            } else {
                pedStatus.clear();
                pedStatus.addAll(controller.listarNomesPorMesaPedidoAll(String.valueOf(ped.getMesa()),PED_PRONTO));

                if (pedStatus.size()>0){
                    status = PED_PRONTO;
                } else {
                    status = PED_CX;
                }
            }
            if (nomes.length()>0) {
                nomes = nomes.substring(0, nomes.length() - 2);
            }
            ped.setNome(nomes);
            ped.setStatus(status);
            pedidoReturn.add(ped);
        }

        return pedidoReturn;
    }

    public String resumePedido(ArrayList<String> name, String mesa){
        String listagemPedido = "";

        for (String nome:name) { //roda esse loop para cada nome que existe no pedido

            if (nome.substring(0,1).equals(" ")){//verifica se existe um espaço antes do nome da pessoa,
                nome = nome.substring(1,nome.length());                //caso exista, tira esse espaço do nome antes de consultar
            }

            listagemPedido = listagemPedido + "Pedido do(a) cliente: " + nome + "\n\n";
            pedido = controller.listarPedidoPorNomeMesa(mesa, nome);//consulta o pedido pela mesa e nome da pessoa

            if (verificaPratoBebidaPedido(pedido,0)) {//verifica se existe pelo menos um prato no pedido
                listagemPedido = listagemPedido + "Pratos:" + "\n";

                for (Pedidos ped : pedido) {////Roda esse foreach em quantos pedidos existir pro nome da pessoa consultada

                    if (!ped.getPrato().equals("")) {//verifica se existe um prato para cada uma das linhas do pedido
                        pratos = controller.listarPratosNome(ped.getPrato(),1).get(0);
                        Double preco = Double.parseDouble(ped.getQuant_prato()) * Double.parseDouble(pratos.getPreco_venda());
                        String precoForm = UtilRestaurante.formatarValorDecimal(preco);
                        listagemPedido = listagemPedido + "Qt: " + ped.getQuant_prato() + " - " + pratos.getNome() + " - R$ " + precoForm + "\n";
                    }
                    if (!ped.getAdicional().equals("")) {//verifica se existe pelo menos um id de adicional pro prato pedido
                        String adicional[] = ped.getAdicional().split(",");
                        for (String nomeAd : adicional) {
                            //ingredientes = controller.listarIngredientes(Integer.parseInt(nomeAd)).get(0);
                            ingredientes = controller.listarIngredientesNome(nomeAd,0).get(0);
                            Double preco = Double.parseDouble(ped.getQuant_prato()) * Double.parseDouble(ingredientes.getPreco_ad());
                            String precoForm = UtilRestaurante.formatarValorDecimal(preco);
                            listagemPedido = listagemPedido + "--> Ad: " + ingredientes.getNome() + " - R$ " + precoForm + "\n";
                        }
                    }
                    if (!ped.getRetirar().equals("")) {//verifica se existe pelo menos um id de retirada pro prato pedido
                        String retirada[] = ped.getRetirar().split(",");
                        for (String nomeRe : retirada) {
                            //ingredientes = controller.listarIngredientes(Integer.parseInt(nomeRe)).get(0);
                            listagemPedido = listagemPedido + "--> Re: " + nomeRe + "\n";
                        }
                    }
                    listagemPedido = listagemPedido + "\n";
                }
            }

            if (verificaPratoBebidaPedido(pedido,1)) {//verifica se existe pelo menos uma bebida no pedido
                listagemPedido = listagemPedido + "Bebidas:" + "\n";

                for (Pedidos ped : pedido) {////Roda esse foreach em quantos pedidos existir pro nome da pessoa consultada

                    if (!ped.getBebida().equals("")) {//verifica se existe pelo menos uma bebida pro pedido
                        bebidas = controller.listarBebidasNome(ped.getBebida()).get(0);
                        Double preco = Double.parseDouble(ped.getQuant_bebida()) * Double.parseDouble(bebidas.getPreco_venda());
                        String precoForm = UtilRestaurante.formatarValorDecimal(preco);
                        listagemPedido = listagemPedido + "Qt: " + ped.getQuant_bebida() + " - " + bebidas.getNome() + " - R$ " + precoForm + "\n";
                    }
                }
            }

            listagemPedido = listagemPedido.substring(0,listagemPedido.length()-1);

            if (!pedido.get(0).getObs().equals("")) {//verifica se o prato possui alguma observação, caso sim adiciona essa obs
                listagemPedido = listagemPedido + "\n\n" + "Observações:" + "\n";
                listagemPedido = listagemPedido + pedido.get(0).getObs();
            }

            listagemPedido = listagemPedido + "\n\n";

        }

        listagemPedido = listagemPedido.substring(0,listagemPedido.length()-2);

        return listagemPedido;
    }

    private boolean verificaPratoBebidaPedido(ArrayList<Pedidos> ped, int colunaVerificar) { //verifica se existe alguma bebida no pedido
        boolean sucesso = false;

        for (int i = 0; i < ped.size(); i++){
            if ((!ped.get(i).getBebida().equals("")) && colunaVerificar==1){
                sucesso = true;
            } else if ((!ped.get(i).getPrato().equals("")) && colunaVerificar==0){
                sucesso = true;
            }
        }

        return sucesso;
    }
}
