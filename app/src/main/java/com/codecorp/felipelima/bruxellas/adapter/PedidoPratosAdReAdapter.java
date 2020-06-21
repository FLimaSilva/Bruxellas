package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.util.TinyDB;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.util.ArrayList;

public class PedidoPratosAdReAdapter extends ArrayAdapter<Pratos> implements View.OnClickListener{

    private TinyDB tinyDB;
    private static final String LISTA_PRATOS = "lista_pratos_pedidos";
    private ArrayList<AdicionalRetiradaPedido> adRePedido;
    private ArrayList<Pratos> pratosPedido;
    private ArrayList<Pratos> pratosCB;
    private RestauranteController controller;
    private ViewHolder linha;

    private static class ViewHolder{
        TextView textPrNome;
        TextView textAdNomes;
        TextView textReNomes;
        TextView textPrecoTotPr;
        TextView textPrQuant;

        CheckBox CbViagem;
        CheckBox CbDepois;

        Pratos pratos;
        AdicionalRetiradaPedido adicionalRetiradaPedido;
        Ingredientes ingredientes;

        int position;
    }

    public PedidoPratosAdReAdapter(@NonNull Context context, ArrayList<Pratos> pratos, ArrayList<AdicionalRetiradaPedido> adicionais) {
        super(context,R.layout.list_view_add_itens_pratos,pratos);

        tinyDB = new TinyDB(context);

        this.adRePedido = adicionais;
        this.pratosPedido = pratos;
        controller = new RestauranteController(context);
    }

    public void atualizarLista(ArrayList<Pratos> novosPratos,ArrayList<AdicionalRetiradaPedido> novosAdRe) {
        this.pratosPedido.clear();
        this.pratosPedido.addAll(novosPratos);

        this.adRePedido.clear();
        this.adRePedido.addAll(novosAdRe);

        notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void onClick(View view) {

        ViewHolder linha = (ViewHolder) view.getTag();

        boolean CbDepois=linha.CbDepois.isChecked();
        boolean CbViagem=linha.CbViagem.isChecked();

        if (CbDepois && CbViagem){
            linha.pratos.setCond_prato(3);
        } else if (CbDepois){
            linha.pratos.setCond_prato(1);
        } else if (CbViagem){
            linha.pratos.setCond_prato(2);
        } else {
            linha.pratos.setCond_prato(0);
        }

        pratosCB = tinyDB.getListPratos(LISTA_PRATOS);
        pratosCB.set(linha.position,linha.pratos);
        tinyDB.putListPratos(LISTA_PRATOS,pratosCB);
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View dataSet,
                        @NonNull ViewGroup parent) {

        if (dataSet == null){
            linha = new ViewHolder();

            LayoutInflater pedidosPratos = LayoutInflater.from(getContext());
            dataSet = pedidosPratos.inflate(R.layout.list_view_add_itens_pratos,
                    parent,false);

            linha.textPrNome = dataSet.findViewById(R.id.textPrNome);
            linha.textPrQuant = dataSet.findViewById(R.id.textQuant);
            linha.textAdNomes = dataSet.findViewById(R.id.textAdNomes);
            linha.textReNomes = dataSet.findViewById(R.id.textReNomes);
            linha.textPrecoTotPr = dataSet.findViewById(R.id.textPrecoTotPr);
            linha.CbDepois = dataSet.findViewById(R.id.CbDepois);
            linha.CbViagem = dataSet.findViewById(R.id.CbViagem);

            dataSet.setTag(linha);
        } else {
            linha = (ViewHolder) dataSet.getTag();
        }

        linha.pratos = getItem(position);
        linha.position = position;
        linha.adicionalRetiradaPedido = adRePedido.get(position);

        //linha.ingredientes = controller.listarIngredientes(position).get(0);

        linha.textPrNome.setText(linha.pratos.getNome());
        linha.textPrQuant.setText(linha.pratos.getQuant());
        double precoAd = 0.0;

        if (linha.adicionalRetiradaPedido.getAdicional().equals("")){
            linha.textAdNomes.setVisibility(View.GONE);
        } else {

            String separaAd[] = linha.adicionalRetiradaPedido.getAdicional().split(",");
            String nomesAd = "Adicionais: ";
            for (String nome: separaAd) {
                linha.ingredientes = controller.listarIngredientesNome(nome,0).get(0);
                nomesAd = nomesAd + linha.ingredientes.getNome() + ", ";
                precoAd = precoAd + Double.parseDouble(linha.ingredientes.getPreco_ad());
            }
            nomesAd = nomesAd.substring(0, nomesAd.length() - 2);
            Double precoQtAd = precoAd * Double.parseDouble(linha.pratos.getQuant());
            String adNomePreco = nomesAd + " - R$ " + UtilRestaurante.formatarValorDecimal(precoQtAd);
            linha.textAdNomes.setText(adNomePreco);
        }
        if (linha.adicionalRetiradaPedido.getRetirada().equals("")){
            linha.textReNomes.setVisibility(View.GONE);
        } else {

            String separaRe[] = linha.adicionalRetiradaPedido.getRetirada().split(",");
            String nomesRe = "Retiradas: ";

            for (String nome: separaRe) {
                //linha.ingredientes = controller.listarIngredientes(Integer.parseInt(nome)).get(0);
                //nomesRe = nomesRe + linha.ingredientes.getNome() + ", ";
                nomesRe = nomesRe + nome + ", ";
            }
            nomesRe = nomesRe.substring(0,nomesRe.length() - 2);

            linha.textReNomes.setText(nomesRe);
        }
        double precoTot = 0.0;
        precoTot = precoAd + Double.parseDouble(linha.pratos.getPreco_venda());
        precoTot = precoTot * Double.parseDouble(linha.pratos.getQuant());
        String sPrecoTot = "R$ " + UtilRestaurante.formatarValorDecimal(precoTot);
        linha.textPrecoTotPr.setText(sPrecoTot);

        switch (linha.pratos.getCond_prato()){
            case 1:
                linha.CbDepois.setChecked(true);
                break;
            case 2:
                linha.CbViagem.setChecked(true);
                break;
            case 3:
                linha.CbDepois.setChecked(true);
                linha.CbViagem.setChecked(true);
                break;
        }

        linha.CbViagem.setTag(linha);
        linha.CbViagem.setOnClickListener(this);
        linha.CbDepois.setTag(linha);
        linha.CbDepois.setOnClickListener(this);

        return dataSet;
    }
}
