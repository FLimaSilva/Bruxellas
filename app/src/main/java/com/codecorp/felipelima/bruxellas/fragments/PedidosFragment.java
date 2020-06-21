package com.codecorp.felipelima.bruxellas.fragments;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.PedidosAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.util.ListarAsyncTask;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.codecorp.felipelima.bruxellas.view.MainActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidosFragment extends Fragment {

    private View view;
    private ListView listView;
    private ArrayList<Pedidos> dados = new ArrayList<>();
    private ArrayList<Pedidos> dadosNome = new ArrayList<>();
    private PedidosAdapter adapter;

    RestauranteController controller;

    private static final String PED_COZ = "cozinha";
    private static final String PED_PRONTO = "pronto";
    private static final String PED_CX = "caixa";


    public PedidosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        controller = new RestauranteController(getContext());

        final Activity a = getActivity();
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        listView = view.findViewById(R.id.listViewPedidos);

        return view;
    }

    @Override
    public void onResume() {
        dadosNome.clear();

        popularListView(); //Enche de informações o array  dados de objetos do tipo Pedidos
        adapter = new PedidosAdapter(dadosNome,getContext());
        listView.setAdapter(adapter);

        super.onResume();
    }


    public void popularListView(){

        dados = controller.listarMesasPedido();
        ArrayList<Pedidos> pedNome = new ArrayList<>();
        ArrayList<Pedidos> pedStatus = new ArrayList<>();
        String nomes;
        String status;

        for (Pedidos ped:dados) {

            nomes="";
            status="";
            pedNome.clear();
            pedStatus.clear();

            pedNome.addAll(controller.listarNomesPorMesaPedido(String.valueOf(ped.getMesa())));

            for (Pedidos nome:pedNome) {
                nomes = nomes + nome.getNome() + ", ";
            }

            //pedStatus.addAll(controller.listarNomesPorMesaPedidoAll(String.valueOf(ped.getMesa()),PED_COZ));
            pedStatus.addAll(controller.listarNomesPorMesaPedidoAll(String.valueOf(ped.getMesa()),PED_PRONTO));

            if (pedStatus.size()>0){
                //status = PED_COZ;
                status = PED_PRONTO;
            } else {
                pedStatus.clear();
                //pedStatus.addAll(controller.listarNomesPorMesaPedidoAll(String.valueOf(ped.getMesa()),PED_PRONTO));
                pedStatus.addAll(controller.listarNomesPorMesaPedidoAll(String.valueOf(ped.getMesa()),PED_COZ));

                if (pedStatus.size()>0){
                    //status = PED_PRONTO;
                    status = PED_COZ;
                } else {
                    status = PED_CX;
                }
            }

        nomes = nomes.substring(0,nomes.length()-2);
        ped.setNome(nomes);
        ped.setStatus(status);
        dadosNome.add(ped);
        }



    }

}
