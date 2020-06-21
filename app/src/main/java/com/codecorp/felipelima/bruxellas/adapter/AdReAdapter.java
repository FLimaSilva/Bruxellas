package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetirada;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class AdReAdapter extends ArrayAdapter<Ingredientes> implements View.OnClickListener {

    ViewHolder linha;
    private ArrayList<AdicionalRetirada> adicionalRetiradaPedido;
    private TinyDB tinyDB;
    private static final String LISTA_AD_RE = "lista_ingredientes_AdRe";

    private static class ViewHolder{
        TextView textIngNome;
        TextView textIngPreco;
        TextView textIngQuantSel;
        ImageView imageViewRe;
        ImageView imageViewAd;

        Ingredientes ingredientes;
    }

    public AdReAdapter(@NonNull Context context, ArrayList<Ingredientes> dados) {
        super(context, R.layout.list_view_ad_re, dados);
    }

    @NonNull
    @Override
    public View getView(final int position,
                        @Nullable View dataSet,
                        @NonNull ViewGroup parent) {

        tinyDB = new TinyDB(getContext());

        if (dataSet == null) {
            linha = new ViewHolder();

            LayoutInflater mesasAbertas = LayoutInflater.from(getContext());
            dataSet = mesasAbertas.inflate(R.layout.list_view_ad_re,
                    parent,false);

            linha.textIngNome = dataSet.findViewById(R.id.textIngNome);
            linha.textIngPreco = dataSet.findViewById(R.id.textIngPreco);
            linha.textIngQuantSel = dataSet.findViewById(R.id.textIngQuantSel);
            linha.imageViewRe =  dataSet.findViewById(R.id.imageViewRe);
            linha.imageViewAd = dataSet.findViewById(R.id.imageViewAd);

            linha.textIngNome.setTag(linha);
            linha.textIngPreco.setTag(linha);
            linha.textIngQuantSel.setTag(linha);
            linha.imageViewRe.setTag(linha);
            linha.imageViewAd.setTag(linha);

            dataSet.setTag(linha);
        }else {
            linha = (ViewHolder) dataSet.getTag();
        }

        linha.ingredientes = getItem(position);

        linha.textIngNome.setText(linha.ingredientes.getNome());
        String precoAdRe = "R$ " + UtilRestaurante.formatarValorDecimal(linha.ingredientes.getPreco_ad());
        linha.textIngPreco.setText(precoAdRe);

        if (tinyDB.containsKey(LISTA_AD_RE)){
            adicionalRetiradaPedido = tinyDB.getListAdRe(LISTA_AD_RE);

            if (adicionalRetiradaPedido.size()>0){
                for (int i = 0; i==0 || i < adicionalRetiradaPedido.size(); i++){
                    AdicionalRetirada adicionalRetirada = adicionalRetiradaPedido.get(i);

                    if (adicionalRetirada.getId_ingrediente() == linha.ingredientes.getId()){
                        linha.textIngQuantSel.setText(String.valueOf(adicionalRetirada.getQuant_ingrediente()));
                        break;
                    } else {
                        linha.textIngQuantSel.setText("0");
                    }
                }
            }
        } else {
            adicionalRetiradaPedido = new ArrayList<AdicionalRetirada>();
            linha.textIngQuantSel.setText("0");
        }

        //linha.imageViewRe.setImageResource(R.drawable.ic_sub_red_24dp);
        //linha.imageViewAd.setImageResource(R.drawable.ic_add_box_green_24dp);

        linha.imageViewRe.setImageResource(R.drawable.ic_sub_gray_24dp);
        linha.imageViewAd.setImageResource(R.drawable.ic_add_box_orange_24dp);

        linha.imageViewRe.setOnClickListener(this);
        linha.imageViewRe.setTag(linha);
        linha.imageViewAd.setOnClickListener(this);
        linha.imageViewAd.setTag(linha);

        return dataSet;
    }

    @Override
    public void onClick(View view) {

        ViewHolder linha = (ViewHolder) view.getTag();

        int valor;

        switch (view.getId()) {

            case R.id.imageViewRe:
                valor = Integer.parseInt(linha.textIngQuantSel.getText().toString());
                if (valor>-1) {
                    valor = valor - 1;
                }
                linha.textIngQuantSel.setText(String.valueOf(valor));
                break;

            case R.id.imageViewAd:
                valor = Integer.parseInt(linha.textIngQuantSel.getText().toString());
                if (valor<1) {
                    valor = valor + 1;
                }
                linha.textIngQuantSel.setText(String.valueOf(valor));
                break;
        }

        if (adicionalRetiradaPedido.size()>0){
            for (int i = 0; i ==0 || i < adicionalRetiradaPedido.size(); i++){
                AdicionalRetirada adicionalRetirada = adicionalRetiradaPedido.get(i);

                if (adicionalRetirada.getId_ingrediente() == linha.ingredientes.getId()){
                    adicionalRetiradaPedido.remove(i);
                }
            }
        }

        if (Integer.parseInt(linha.textIngQuantSel.getText().toString()) != 0){
            AdicionalRetirada aR = new AdicionalRetirada();

            aR.setId_ingrediente(linha.ingredientes.getId());
            aR.setQuant_ingrediente(Integer.parseInt(linha.textIngQuantSel.getText().toString()));

            adicionalRetiradaPedido.add(aR);
        }

        tinyDB.putListAdRe(LISTA_AD_RE,adicionalRetiradaPedido);
    }
}
