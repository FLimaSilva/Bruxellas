package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class BebidasAdapter extends ArrayAdapter<Bebidas> implements View.OnClickListener{

    private static class ViewHolder{
        TextView textNome;
        TextView textPreco;
    }

    public BebidasAdapter(@NonNull Context context, ArrayList<Bebidas> dados) {
        super(context, R.layout.list_view_itens, dados);
    }

    @Override
    public void onClick(View view) {

    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View dataSet,
                        @NonNull ViewGroup parent) {

        Bebidas bebidas = getItem(position);
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

        linha.textNome.setText(bebidas.getNome());
        String precoBebida = "R$ " + UtilRestaurante.formatarValorDecimal(bebidas.getPreco_venda());
        linha.textPreco.setText(precoBebida);

        return dataSet;
    }
}

