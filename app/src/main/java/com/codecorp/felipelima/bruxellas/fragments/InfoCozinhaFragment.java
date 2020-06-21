package com.codecorp.felipelima.bruxellas.fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.PedidoCozAdapter;
import com.codecorp.felipelima.bruxellas.model.Pedidos;

import java.io.PipedOutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoCozinhaFragment extends Fragment {

    private View view;
    private GridView gridView;

    private String PREFERENCES = "preferences_user";
    private String DIMENSION_GRID_VIEW_WIDTH = "gridview_width";

    public InfoCozinhaFragment() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Activity a = getActivity();
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info_cozinha, container, false);

        gridView = view.findViewById(R.id.gridViewInfo);

        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES,0);//pega o valor condifigurado na tela
        int columWidth = preferences.getInt(DIMENSION_GRID_VIEW_WIDTH,140);

        ArrayList<Pedidos> pedidos = new ArrayList<>();
        pedidos.add(new Pedidos());
        pedidos.add(new Pedidos());
        pedidos.add(new Pedidos());
        pedidos.add(new Pedidos());
        pedidos.add(new Pedidos());
        pedidos.add(new Pedidos());

        final PedidoCozAdapter adapter = new PedidoCozAdapter(getActivity().getApplicationContext(),pedidos,true);

        gridView.setColumnWidth(columWidth);
        gridView.setAdapter(adapter);

        return view;
    }

}
