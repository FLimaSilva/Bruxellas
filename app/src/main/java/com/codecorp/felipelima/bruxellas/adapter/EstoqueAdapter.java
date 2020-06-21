package com.codecorp.felipelima.bruxellas.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class EstoqueAdapter extends ArrayAdapter<Ingredientes> implements View.OnClickListener{

    RestauranteController controller;

    private static class ViewHolder{
        TextView textDesc;
        TextView textQuantA;
        TextView textQuantM;
        TextView textUN;

        Ingredientes ingredientes;
    }

    public EstoqueAdapter(@NonNull Context context, ArrayList<Ingredientes> dados) {
        super(context, R.layout.list_view_estoque, dados);
        controller = new RestauranteController(context);
    }

    @Override
    public void onClick(View view) {

    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View dataSet,
                        @NonNull ViewGroup parent) {

        ViewHolder linha;

        ArrayList<Ingredientes> ingsAll = controller.listarIngredientes();

        if (dataSet == null) {
            linha = new ViewHolder();

            LayoutInflater mesasAbertas = LayoutInflater.from(getContext());
            dataSet = mesasAbertas.inflate(R.layout.list_view_estoque,
                    parent,false);

            linha.textDesc = dataSet.findViewById(R.id.textDesc);
            linha.textQuantA = dataSet.findViewById(R.id.textQuantA);
            linha.textQuantM = dataSet.findViewById(R.id.textQuantM);
            linha.textUN = dataSet.findViewById(R.id.textUN);

            dataSet.setTag(linha);
        }else {
            linha = (ViewHolder) dataSet.getTag();
        }

        linha.ingredientes = getItem(position);

        if (linha.ingredientes.getQuant() < linha.ingredientes.getQuant_min()) {
            linha.textDesc.setTextColor(Color.RED);
            linha.textQuantA.setTextColor(Color.RED);
            linha.textQuantM.setTextColor(Color.RED);
            linha.textUN.setTextColor(Color.RED);

            SpannableStringBuilder tst = new SpannableStringBuilder();
            tst.append(linha.ingredientes.getNome());
            tst.setSpan(new StyleSpan(Typeface.BOLD), 0, linha.ingredientes.getNome().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.textDesc.setText(tst);

            tst = new SpannableStringBuilder();
            tst.append(String.valueOf((int) linha.ingredientes.getQuant()));
            tst.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf((int) linha.ingredientes.getQuant()).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.textQuantA.setText(tst);

            tst = new SpannableStringBuilder();
            tst.append(String.valueOf((int) linha.ingredientes.getQuant_min()));
            tst.setSpan(new StyleSpan(Typeface.BOLD), 0, String.valueOf((int) linha.ingredientes.getQuant_min()).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.textQuantM.setText(tst);

            tst = new SpannableStringBuilder();
            tst.append(linha.ingredientes.getUnidade());
            tst.setSpan(new StyleSpan(Typeface.BOLD), 0, linha.ingredientes.getUnidade().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            linha.textUN.setText(tst);
        } else {
            linha.textDesc.setTextColor(Color.rgb(115, 115, 115));
            linha.textQuantA.setTextColor(Color.rgb(115, 115, 115));
            linha.textQuantM.setTextColor(Color.rgb(115, 115, 115));
            linha.textUN.setTextColor(Color.rgb(115, 115, 115));

            linha.textDesc.setText(linha.ingredientes.getNome());
            linha.textQuantA.setText(String.valueOf((int) linha.ingredientes.getQuant()));
            linha.textQuantM.setText(String.valueOf((int) linha.ingredientes.getQuant_min()));
            linha.textUN.setText(linha.ingredientes.getUnidade());
        }

        return dataSet;
    }
}

