package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class PratosAdapter extends ArrayAdapter<Pratos> implements View.OnClickListener{

    private static class ViewHolder{
        TextView textNome;
        TextView textPreco;
    }

    public PratosAdapter(@NonNull Context context, ArrayList<Pratos> dados) {
        super(context, R.layout.list_view_itens, dados);
    }

    @Override
    public void onClick(View view) {

        /*int posicao = (Integer) view.getTag();

        Object object = getItem(posicao);

        Pratos pratos = (Pratos) object;*/
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View dataSet,
                        @NonNull ViewGroup parent) {

        Pratos pratos = getItem(position);
        ViewHolder linha;

        if (dataSet == null) {
            linha = new ViewHolder();

            LayoutInflater mesasAbertas = LayoutInflater.from(getContext());
            dataSet = mesasAbertas.inflate(R.layout.list_view_itens,
                    parent,false);

            linha.textNome = dataSet.findViewById(R.id.textNome);
            linha.textPreco = dataSet.findViewById(R.id.textPreco);

            dataSet.setTag(linha);
        }else {
            linha = (ViewHolder) dataSet.getTag();
        }

        linha.textNome.setText(pratos.getNome());
        String precoPrato = "R$ " + UtilRestaurante.formatarValorDecimal(pratos.getPreco_venda());
        linha.textPreco.setText(precoPrato);

        return dataSet;
    }
}
