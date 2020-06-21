package com.codecorp.felipelima.bruxellas.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PedidoCozAdapter extends ArrayAdapter<Pedidos>{

    private static final String PED_COZ = "cozinha";

    private ViewHolder linha;
    private ArrayList<Pedidos> pedidosCoz;
    private RestauranteController controller;
    private String prato;
    private boolean telaInfo;

    private String PREFERENCES = "preferences_user";
    private String DIMENSION_GRID_VIEW_HEIGHT = "gridview_height";

    private static class ViewHolder {

        TextView textCzMesa;
        TextView textCzData;
        TextView textCzPedido;
        TextView textColor;
        TextView textColor2;

        Pedidos pedidos;

        ConstraintLayout cLayout;
    }

    public PedidoCozAdapter(@NonNull Context context, @NonNull ArrayList<Pedidos> pedido) {
        super(context, R.layout.cozinha_grid_view, pedido);

        controller = new RestauranteController(context);
        this.pedidosCoz = pedido;
        this.telaInfo = false;
    }

    public PedidoCozAdapter(@NonNull Context context, @NonNull ArrayList<Pedidos> pedido, @NonNull boolean telaInfo) {
        super(context, R.layout.cozinha_grid_view, pedido);

        controller = new RestauranteController(context);
        this.pedidosCoz = pedido;
        this.telaInfo = telaInfo;
    }

    public void atualizarLista(ArrayList<Pedidos> novosPedidos) {
        this.pedidosCoz.clear();
        this.pedidosCoz.addAll(novosPedidos);

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

        if (dataSet == null) {
            linha = new ViewHolder();

            LayoutInflater pedidosCz = LayoutInflater.from(getContext());

            dataSet = pedidosCz.inflate(R.layout.cozinha_grid_view,
                    parent,
                    false);

            linha.textCzMesa = dataSet.findViewById(R.id.textCzMesa);
            linha.textCzData = dataSet.findViewById(R.id.textCzData);
            linha.textCzPedido = dataSet.findViewById(R.id.textCzPedido);
            linha.textColor = dataSet.findViewById(R.id.textColor);
            linha.textColor2 = dataSet.findViewById(R.id.textColor2);
            linha.cLayout = dataSet.findViewById(R.id.cLayoutCoz);

            dataSet.setTag(linha);
        }else {
            linha = (ViewHolder) dataSet.getTag();
        }

        linha.pedidos = getItem(position);

        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES,0);//pega o valor condifigurado na tela
        int columWidth = preferences.getInt(DIMENSION_GRID_VIEW_HEIGHT,140);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                columWidth);
        linha.cLayout.setLayoutParams(lp);

        prato = "";
        String title = "";
        String dataHora = "";
        boolean adic = false;
        boolean ret = false;
        boolean depois = false;
        boolean viagem = false;
        ArrayList<Pedidos> pedNome = new ArrayList<>();

        if (!telaInfo) {
            pedNome = controller.listarNomesPorMesaPedidoAll(String.valueOf(linha.pedidos.getMesa()), PED_COZ);//vê quantos nomes existem na mesa que foi consultada no BD

            for (Pedidos ped : pedNome) {
                if (!ped.getPrato().equals("")) {
                    prato = prato + "Qt: " + ped.getQuant_prato() + " - " + ped.getPrato() + "\n";
                }
            }

            ArrayList<Pedidos> pedPratosNoGroup = controller.listarNomesPorMesaPedidoAllNoGroup(String.valueOf(linha.pedidos.getMesa()), PED_COZ, 0);

            for (Pedidos ped : pedPratosNoGroup) {
                adic = adic || (!ped.getAdicional().equals(""));
                ret = ret || (!ped.getRetirar().equals(""));

                depois = depois || (ped.getCondicao_prato() == 1) || (ped.getCondicao_prato() == 3);
                viagem = viagem || (ped.getCondicao_prato() == 2) || (ped.getCondicao_prato() == 3);
            }

            prato = prato.substring(0,prato.length()-1);//retira o último \n

            title = (position+1) + "° - (" + String.valueOf(pedNome.size()) + ") " + "Mesa " + linha.pedidos.getMesa();

            dataHora = linha.pedidos.getData_hora();
        }
        else {
            title = "(1) Mesa "+position;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM");
            dataHora = sdf.format(new Date());
            switch (position){
                case 0:
                    adic = true;
                    prato = "A cor verde na lateral quer dizer que pelo menos um prato da mesa tem adicional";
                    break;
                case 1:
                    ret = true;
                    prato = "A cor vermelha na lateral quer dizer que pelo menos um prato da mesa tem retirada";
                    break;
                case 2:
                    adic = true;
                    ret = true;
                    prato = "A cor azul na lateral quer dizer que existem pratos na mesa com adicionais e retiradas";
                    break;
                case 3:
                    depois = true;
                    prato = "A cor laranja na lateral quer dizer que pelo menos um prato da mesa é para ser feito depois";
                    break;
                case 4:
                    viagem = true;
                    prato = "A cor verde escuro na lateral quer dizer que pelo menos um prato da mesa é para viagem";
                    break;
                case 5:
                    depois = true;
                    viagem = true;
                    prato = "A cor roxa na lateral quer dizer que existem pratos na mesa que são para ser feitos depois e para viagem";
                    break;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //cores de adicionais e retiradas
            if (adic && ret){
                linha.textColor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_grid_view_blue));

            }  else if (ret){
                linha.textColor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_grid_view_red));

            } else if (adic){
                linha.textColor.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_grid_view_green));

            } else {
                linha.textColor.setVisibility(View.GONE);
            }
            //cores de pratos para viagem e pratos para depois
            if (viagem && depois){
                linha.textColor2.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_grid_view_deep_purple));

            }  else if (viagem){
                linha.textColor2.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_grid_view_lime));

            } else if (depois){
                linha.textColor2.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.shape_grid_view_orange));

            } else {
                linha.textColor2.setVisibility(View.GONE);
            }

        } else {
            UtilRestaurante.showMensagem(getContext(),"Erro de compatibilidade de sistema!");
        }

        linha.textCzMesa.setText(title);
        linha.textCzData.setText(dataHora);
        linha.textCzPedido.setText(prato);

        return dataSet;
    }
}
