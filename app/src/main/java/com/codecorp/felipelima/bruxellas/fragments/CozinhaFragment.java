package com.codecorp.felipelima.bruxellas.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.PedidoCozAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.util.AlterarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CozinhaFragment extends Fragment {

    private static final String PED_COZ = "cozinha";
    private static final String PED_PRONTO = "pronto";

    private View view;
    private GridView gridView;
    private RestauranteController controller;
    private ArrayList<Pedidos> pedPratosMesa;
    private PedidoCozAdapter adapter;

    private String PREFERENCES = "preferences_user";
    private String DIMENSION_GRID_VIEW_WIDTH = "gridview_width";
    private int count=0;
    private boolean sucesso=true;

    public CozinhaFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser){
            Activity a = getActivity();
            if (a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        controller = new RestauranteController(getContext());

        Activity a = getActivity();
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        view = inflater.inflate(R.layout.fragment_cozinha, container, false);

        gridView = view.findViewById(R.id.gridViewCz);

        final ArrayList<Pedidos> pedidosCoz = controller.listarMesasPedidoLimite(6,PED_COZ);

        adapter = new PedidoCozAdapter(getActivity().getApplicationContext(),pedidosCoz);
        gridView.setAdapter(adapter);

        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES,0);//pega o valor condifigurado na tela
        int columWidth = preferences.getInt(DIMENSION_GRID_VIEW_WIDTH,140);
        gridView.setColumnWidth(columWidth);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //parei aqui, agora tem que mostrar os pratos com os respectivos ad e re sem se importar com nomes e valores
                //e não pode mais agrupar as quantidades dos pratos, pois pode ser que para cada prato igual, os ad e re são diferentes

                String nomeUser = "";

                ArrayList<Pedidos> pedPratosMesa = controller.listarNomesPorMesaPedidoAllNoGroup(String.valueOf(pedidosCoz.get(i).getMesa()),PED_COZ,2);
                if (pedPratosMesa.size()>0) {
                    nomeUser = pedPratosMesa.get(0).getNome_usuario();
                }
                String listaPratosAdReAgora = resumePedido(pedPratosMesa);

                pedPratosMesa = controller.listarNomesPorMesaPedidoAllNoGroup(String.valueOf(pedidosCoz.get(i).getMesa()),PED_COZ,1);
                if (pedPratosMesa.size()>0 && (nomeUser != null)){
                    nomeUser = pedPratosMesa.get(0).getNome_usuario();
                }
                String listaPratosAdReDepois = resumePedido(pedPratosMesa);

                if (!listaPratosAdReDepois.equals("")){
                    if (!listaPratosAdReAgora.equals("")){
                        listaPratosAdReAgora = listaPratosAdReAgora + "\n\n";
                    }
                    listaPratosAdReAgora = listaPratosAdReAgora + "----------------------------------------------------\n\n";
                }

                String listaTodosPratos = listaPratosAdReAgora+listaPratosAdReDepois;

                listaTodosPratos = listaTodosPratos + "\n\nO pedido foi realizado por: "+nomeUser;

                AlertDialog.Builder dialogCozPratos = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog
                dialogCozPratos.setTitle("Detalhes dos pratos"); //Configura título e mensagem
                dialogCozPratos.setMessage(listaTodosPratos);
                dialogCozPratos.setCancelable(true); //Configura o cancelamento
                dialogCozPratos.setIcon(R.drawable.ic_kitchen_black_24dp); //Configura ícone

                dialogCozPratos.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogCozPratos.create();
                dialogCozPratos.show();

            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                pedPratosMesa = controller.listarNomesPorMesaPedidoAllNoGroup(String.valueOf(pedidosCoz.get(i).getMesa()),PED_COZ,2);

                AlertDialog.Builder dialogFechaPratos = new AlertDialog.Builder(view.getRootView().getContext()); //Cria uma Alert dialog
                dialogFechaPratos.setTitle("Finalizando pratos"); //Configura título e mensagem
                dialogFechaPratos.setMessage("Deseja comunicar o garçom que este(s) prato(s) estão prontos?");
                dialogFechaPratos.setCancelable(true); //Configura o cancelamento
                dialogFechaPratos.setIcon(R.drawable.ic_kitchen_black_24dp); //Configura ícone

                dialogFechaPratos.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean sucess = true;
                        count = 0;
                        sucesso = true;

                        for (Pedidos ped:pedPratosMesa) {
                            try {
                                ped.setStatus(PED_PRONTO);
                                sucess = sucess && controller.alterarPedidoCoz(ped);

                                if (sucess){
                                    AlterarAsyncTask task = new AlterarAsyncTask(ped, getContext(), true, new AlterarAsyncTask.AsyncResponse() {
                                        @Override
                                        public void processFinish(boolean output) {
                                            verificaQuantPratos(output);
                                        }
                                    });
                                    task.execute();
                                }
                            } catch (Exception e){
                                Log.e("Servidor", "Erro ao alterar pela cozinha: "+e.getMessage());
                            }
                        }

                        if (pedPratosMesa.size()==0){
                            UtilRestaurante.showMensagem(getContext(),"Todos os pratos de agora já foram entregues!");
                        }
                    }
                });

                dialogFechaPratos.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogFechaPratos.create();
                dialogFechaPratos.show();

                return true;
            }
        });

        return view;
    }

    private void verificaQuantPratos(boolean sucess) {
        count = count + 1;
        sucesso = sucesso && sucess;

        if (count >= pedPratosMesa.size()){
            if (sucesso) {
                UtilRestaurante.showMensagem(getContext(),
                        "O garçom foi avisado que o(s) prato(s) da mesa " + pedPratosMesa.get(0).getMesa() + " estão prontos!");
                adapter.atualizarLista(controller.listarMesasPedidoLimite(6, PED_COZ));

            } else {
                UtilRestaurante.showMensagem(getContext(),
                        "Ocorreu algum problema ao avisar o garçom!");
            }
        }
    }

    public String resumePedido(ArrayList<Pedidos> pedidoFinal){

        String listagemPedido = "";

        for (Pedidos ped : pedidoFinal) {////Roda esse foreach em quantos pedidos existir pro nome da pessoa consultada
            //listagemPedido = listagemPedido + "Prato \n";

            if (!ped.getPrato().equals("")) {//verifica se existe um prato para cada uma das linhas do pedido
                listagemPedido = listagemPedido + "Prato \n";
                listagemPedido = listagemPedido + "-> Qt: " + ped.getQuant_prato() + " - " + ped.getPrato() + condicaoPrato(ped.getCondicao_prato()) + "\n";
            }
            if (!ped.getAdicional().equals("")) {//verifica se existe pelo menos um id de adicional pro prato pedido
                String adicional[] = ped.getAdicional().split(",");
                for (String nomeAd : adicional) {
                    Ingredientes ingredientes = controller.listarIngredientesNome(nomeAd,0).get(0);
                    listagemPedido = listagemPedido + "--> Ad: " + ingredientes.getNome() + "\n";
                }
            }
            if (!ped.getRetirar().equals("")) {//verifica se existe pelo menos um id de retirada pro prato pedido
                String retirada[] = ped.getRetirar().split(",");
                for (String nomeRe : retirada) {
                    //Ingredientes ingredientes = controller.listarIngredientes(Integer.parseInt(nomeRe)).get(0);
                    listagemPedido = listagemPedido + "--> Re: " + nomeRe + "\n";
                }
            }
            if ((!ped.getObs().equals("")) && (!ped.getObs().contains("null"))) {//verifica se o prato possui alguma observação, caso sim adiciona essa obs
                listagemPedido = listagemPedido + "\nObservações:" + "\n";
                listagemPedido = listagemPedido + ped.getObs() + "\n";
            }
            listagemPedido = listagemPedido + "\n";
        }

        if (!listagemPedido.equals("")) {
            listagemPedido = listagemPedido.substring(0, listagemPedido.length() - 2);
        }
        return listagemPedido;
    }

    private String condicaoPrato(int cond){
        String condicao="";
        if (cond>=2){
            condicao = " - P/ Viagem";
        }
        return condicao;
    }

}
