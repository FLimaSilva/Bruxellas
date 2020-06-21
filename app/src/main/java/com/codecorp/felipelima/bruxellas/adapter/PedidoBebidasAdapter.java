package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.database.DataSetObserver;
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

public class PedidoBebidasAdapter extends ArrayAdapter<Bebidas> {

    private ArrayList<Bebidas> bebidasPedidas;
    private ViewHolder linha;

    private static class ViewHolder{
        TextView textBeNome;
        TextView textNaoUsa;
        TextView textNaoUsa2;
        TextView textPrecoTotBe;
        TextView textBeQuant;

        Bebidas bebidas;
    }

    public PedidoBebidasAdapter(@NonNull Context context, ArrayList<Bebidas> bebidas) {
        super(context, R.layout.list_view_add_itens, bebidas);

        this.bebidasPedidas = bebidas;
    }

    public void atualizarLista(ArrayList<Bebidas> novasBebidas) {
        this.bebidasPedidas.clear();
        this.bebidasPedidas.addAll(novasBebidas);

        notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View dataSet,
                        @NonNull ViewGroup parent) {
        if (dataSet == null){
            linha = new ViewHolder();

            LayoutInflater bebidasPedidas = LayoutInflater.from(getContext());
            dataSet = bebidasPedidas.inflate(R.layout.list_view_add_itens,
                    parent,false);

            linha.textBeNome = dataSet.findViewById(R.id.textPrNome);
            linha.textBeQuant = dataSet.findViewById(R.id.textQuant);
            linha.textNaoUsa = dataSet.findViewById(R.id.textAdNomes);
            linha.textNaoUsa2 = dataSet.findViewById(R.id.textReNomes);
            linha.textPrecoTotBe = dataSet.findViewById(R.id.textPrecoTotPr);

            dataSet.setTag(linha);
        } else {
            linha = (ViewHolder) dataSet.getTag();
        }

        linha.bebidas = getItem(position);

        linha.textBeNome.setText(linha.bebidas.getNome());
        linha.textBeQuant.setText(linha.bebidas.getQuant());

        linha.textNaoUsa.setVisibility(View.GONE);
        linha.textNaoUsa2.setVisibility(View.GONE);

        double precoTot = 0.0;
        precoTot = Double.parseDouble(linha.bebidas.getPreco_venda()) * Double.parseDouble(linha.bebidas.getQuant());

        String sPrecoTot = "R$ " + UtilRestaurante.formatarValorDecimal(precoTot);
        linha.textPrecoTotBe.setText(sPrecoTot);

        return dataSet;
    }
}
