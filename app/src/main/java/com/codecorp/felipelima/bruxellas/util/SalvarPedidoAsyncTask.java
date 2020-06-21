package com.codecorp.felipelima.bruxellas.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.datamodel.RestauranteDataModel;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SalvarPedidoAsyncTask extends AsyncTask<String, String, String> {

    private Context context;
    private ProgressDialog progressDialog;
    private ArrayList<Pedidos> pedido;
    private ArrayList<Pedidos> pedAltera;
    private RestauranteController controller;
    private AsyncResponse asyncResponse;
    private TinyDB tinyDB;
    private boolean alterar;
    private JsonArray jsonArray = new JsonArray();
    private JsonArray jsonArrayBeb = new JsonArray();
    private ArrayList<Pratos> pratosPedidos;
    private ArrayList<AdicionalRetiradaPedido> adRePedidos;
    private ArrayList<Bebidas> bebidasPedidos;
    private int posNeg;

    private static final String LISTA_ESTOQUE = "lista_estoque_pedido";
    private static final String LISTA_ESTOQUE_DEL = "lista_estoque_pedido_deleta";
    private static final String LISTA_ESTOQUE_BEBIDAS = "lista_estoque_pedido_bebidas";
    private static final String LISTA_ESTOQUE_BEBIDAS_DEL = "lista_estoque_pedido_bebidas_deleta";


    public SalvarPedidoAsyncTask(Context context, ArrayList<Pedidos> pedido, boolean alterar, ArrayList<Pedidos> pedAltera, AsyncResponse asyncResponse) {

        this.context = context;
        this.pedido = pedido;
        this.pedAltera = pedAltera;
        this.asyncResponse = asyncResponse;
        this.alterar = alterar;

        progressDialog = new ProgressDialog(context);
        controller = new RestauranteController(context);
        tinyDB = new TinyDB(context);

    }

    public interface AsyncResponse {
        void processFinish(boolean status);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setCancelable(false);
        if (alterar){
            progressDialog.setTitle("Alterando pedido");
            progressDialog.setMessage("Alterando o pedido no banco de dados");
        } else {
            progressDialog.setTitle("Efetuando pedido");
            progressDialog.setMessage("Salvando o pedido no banco de dados");
        }
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        boolean sucesso=true;

        try {
            if (alterar){// apenas se houverem pedidos para alterar
                for (Pedidos ped: pedAltera) {
                    sucesso = sucesso && controller.alterarPedido(ped);//salva no Sqlite

                    if (sucesso){
                        AlterarAsyncTask alterTask = new AlterarAsyncTask(ped, context, false);
                        alterTask.execute();//Salva no webservice
                    }
                }
            }

            for (Pedidos ped : pedido) {//etapa para salvar o novo pedido no banco de dados
                    sucesso = sucesso && controller.salvarPedido(ped);

                    if (sucesso) {//adiciona o mesmo pedido no WebService
                        IncluirAsyncTask incluirTask = new IncluirAsyncTask(ped, context);
                        incluirTask.execute();
                    }
            }

            Thread.sleep(200);
            publishProgress("Separando os ingredientes dos pratos para o estoque");

            //for (Pedidos ped : pedido) {

                String listasPratos[] = {LISTA_ESTOQUE,LISTA_ESTOQUE_DEL};
                String listasBebidas[] = {LISTA_ESTOQUE_BEBIDAS,LISTA_ESTOQUE_BEBIDAS_DEL};

                for (int j = 0; j < listasPratos.length; j++) {

                    String listaPrato = listasPratos[j];
                    if (tinyDB.containsKey(listaPrato)) {// pega os pratos e ingredientes que estão no shared preferences para pratos e adRe para ser deletados
                        JsonArray pratosPed = tinyDB.getListEstoque(listaPrato);
                        adRePedidos = new ArrayList<>();
                        pratosPedidos = new ArrayList<>();

                        for (int i = 0; i < pratosPed.size(); i++) {

                            JsonObject object = pratosPed.get(i).getAsJsonObject();
                            Pratos prato = new Pratos();
                            AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();

                            prato.setNome(object.get("prato").getAsString());
                            prato.setQuant(object.get("quantidade").getAsString());

                            if (object.has("adicional")) {
                                adRe.setAdicional(object.get("adicional").getAsString());
                                adRe.setQuant_adicional(object.get("quantidade").getAsString());
                            }

                            if (object.has("retirada")) {
                                adRe.setRetirada(object.get("retirada").getAsString());
                                adRe.setQuant_retirada(object.get("quantidadeRe").getAsString());
                            }

                            adRePedidos.add(adRe);
                            pratosPedidos.add(prato);
                        }
                        deduzArrayJsonPratos(pratosPedidos, adRePedidos);
                    }

                    String listaBebida = listasBebidas[j];
                    if (tinyDB.containsKey(listaBebida)) {// pega os pratos e ingredientes que estão no shared preferences para pratos e adRe para ser deletados
                        JsonArray bebidasPed = tinyDB.getListEstoque(listaBebida);
                        bebidasPedidos = new ArrayList<>();

                        for (int i = 0; i < bebidasPed.size(); i++) {
                            JsonObject object = bebidasPed.get(i).getAsJsonObject();
                            Bebidas bebida = new Bebidas();

                            bebida.setNome(object.get("bebida").getAsString());
                            bebida.setQuant(object.get("quantidade").getAsString());

                            bebidasPedidos.add(bebida);
                        }

                        deduzArrayJsonBebidas(bebidasPedidos);
                    }
                }

                /**********************************************************************************/
                /*posNeg = 1;

                // primeiro se trata os ingredientes do prato
                if (!ped.getPrato().equals("")) {
                    ArrayList<Pratos> pratoPed = controller.listarPratosNome(ped.getPrato(), 1);//consulta qual é o prato que foi pedido
                    if (pratoPed.size() == 1) {
                        String pratoPedIngs[] = pratoPed.get(0).getIngredientes().split(",");
                        String pratoPedQtds[] = pratoPed.get(0).getQuant_ing().split(",");

                        for (int i = 0; i < pratoPedIngs.length; i++) {
                            ArrayList<Pratos> subPratos = controller.listarPratosNome(pratoPedIngs[i], 2);//consulta pra saber se o ingrediente é um subprato
                            if (subPratos.size() > 0) {//caso for um subprato
                                String subPratoPedIngs[] = subPratos.get(0).getIngredientes().split(",");//separa os ingredientes do subprato
                                String subPratoPedQtds[] = subPratos.get(0).getQuant_ing().split(",");//separa as quantidades dos ings do subprato

                                for (int j = 0; j < subPratoPedIngs.length; j++) {//roda esse laço a quantidade de subpratos que existirem
                                    JsonObject object1 = new JsonObject();
                                    double subPratoQuantIng = (Double.parseDouble(subPratoPedQtds[j]) * Integer.parseInt(pratoPedQtds[i]) * Integer.parseInt(ped.getQuant_prato())) * posNeg;//calcula a quantidade de ingrediente que vai ser gasta nesse subprato

                                    object1.addProperty("ingrediente", subPratoPedIngs[j]);//adiciona o ingrediente
                                    object1.addProperty("quantidade", subPratoQuantIng);//adiciona a quantidade
                                    jsonArray.add(object1);//adiciona ao array de json
                                }
                            } else {
                                JsonObject object = new JsonObject();
                                String ingrediente = pratoPedIngs[i];
                                double quantIng = (Double.parseDouble(pratoPedQtds[i]) * Integer.parseInt(ped.getQuant_prato())) * posNeg;

                                object.addProperty("ingrediente", ingrediente);
                                object.addProperty("quantidade", quantIng);
                                jsonArray.add(object);
                            }
                        }
                    }

                    //Agora deve tratar os adicionais
                    String pratoPedAds[] = ped.getAdicional().split(",");
                    if (pratoPedAds[0].length() > 0) {
                        for (String pedAds : pratoPedAds) {
                            JsonObject object = new JsonObject();
                            Ingredientes ing = controller.listarIngredientesNome(pedAds).get(0);
                            double quant = (ing.getMedida_ad() * Double.parseDouble(ped.getQuant_prato())) * posNeg;

                            object.addProperty("ingrediente", ing.getNome());
                            object.addProperty("quantidade", quant);
                            jsonArray.add(object);
                        }
                    }

                    //Agora se trata as retiradas do prato
                    String pratoPedRes[] = ped.getRetirar().split(",");
                    String pratoPedIngs[] = pratoPed.get(0).getIngredientes().split(",");
                    String pratoPedQtds[] = pratoPed.get(0).getQuant_ing().split(",");

                    if (pratoPedRes[0].length() > 0) {//Só vai retirar os ingredientes que existem no prato...
                        for (String pedRes : pratoPedRes) {//Roda as retiradas
                            for (int i = 0; i < pratoPedIngs.length; i++) {//Roda os ingredientes do prato
                                if (pedRes.equals(pratoPedIngs[i])) {
                                    JsonObject object = new JsonObject();
                                    double quant = (Double.parseDouble(pratoPedQtds[i]) * Integer.parseInt(ped.getQuant_prato())) * -posNeg;

                                    object.addProperty("ingrediente", pedRes);
                                    object.addProperty("quantidade", quant);
                                    jsonArray.add(object);
                                }
                            }
                        }
                    }
                }

                //Agora se trata as bebidas do pedido
                if (!ped.getBebida().equals("")) {//tem que existir uma bebida, e ter  valor 2 ou 3
                    ArrayList<Bebidas> bebidasPed = controller.listarBebidasNome(ped.getBebida());
                    if (bebidasPed.size() == 1) {
                        JsonObject object = new JsonObject();
                        int quantBeb = (Integer.parseInt(ped.getQuant_bebida())) * posNeg;

                        object.addProperty("bebida", bebidasPed.get(0).getNome());
                        object.addProperty("quantidade", quantBeb);
                        jsonArrayBeb.add(object);
                    }
                }*/
            //}

            Thread.sleep(200);
            publishProgress("Atualizando os ingredientes no estoque");

            AtualizarEstoqueAsyncTask atualizaTask = new AtualizarEstoqueAsyncTask(jsonArray,jsonArrayBeb,context);
            atualizaTask.execute();

            Thread.sleep(200);
            publishProgress("Atualizando a lista de pedidos");

            ListarAsyncTask taskListar = new ListarAsyncTask("pedido",context);//FinalizaPedidoActivity.this);
            taskListar.execute();

        } catch (Exception e) {
            Log.e("Servidor", "Erro ao salvar no servidor: " + e.getMessage());
        }

        Log.i("QuantIngs", "JSON: "+jsonArray.toString());
        Log.i("QuantIngs", "JSONB: "+jsonArrayBeb.toString());

        return String.valueOf(sucesso);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {

        if (alterar) {
            progressDialog.setMessage("Pedido alterado com sucesso");
        } else {
            progressDialog.setMessage("Pedido realizado com sucesso");
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();

        if (s.contains("true")) {
            asyncResponse.processFinish(true);
        } else {
            asyncResponse.processFinish(false);
        }
    }

    private void deduzArrayJsonPratos(ArrayList<Pratos> pratosPed, ArrayList<AdicionalRetiradaPedido> adRePed){

        if (pratosPed.size()>0) {
            posNeg = 1;
            for (int i = 0; i < pratosPed.size(); i++) {
                // primeiro se trata os ingredientes do prato
                ArrayList<Pratos> pratoPed = controller.listarPratosNome(pratosPed.get(i).getNome(), 1);//consulta qual é o prato que foi pedido
                if (!pratosPed.get(i).getNome().equals("")) {
                    if (pratoPed.size() == 1) {
                        String pratoPedIngs[] = pratoPed.get(0).getIngredientes().split(",");
                        String pratoPedQtds[] = pratoPed.get(0).getQuant_ing().split(",");

                        for (int f = 0; f < pratoPedIngs.length; f++) {
                            ArrayList<Pratos> subPratos = controller.listarPratosNome(pratoPedIngs[f], 2);//consulta pra saber se o ingrediente é um subprato
                            if (subPratos.size() > 0) {//caso for um subprato
                                String subPratoPedIngs[] = subPratos.get(0).getIngredientes().split(",");//separa os ingredientes do subprato
                                String subPratoPedQtds[] = subPratos.get(0).getQuant_ing().split(",");//separa as quantidades dos ings do subprato

                                for (int j = 0; j < subPratoPedIngs.length; j++) {//roda esse laço a quantidade de subpratos que existirem
                                    JsonObject object1 = new JsonObject();
                                    double subPratoQuantIng = (Double.parseDouble(subPratoPedQtds[j]) * Integer.parseInt(pratoPedQtds[f]) * Integer.parseInt(pratosPed.get(i).getQuant())) * posNeg;//calcula a quantidade de ingrediente que vai ser gasta nesse subprato

                                    object1.addProperty("ingrediente", subPratoPedIngs[j]);//adiciona o ingrediente
                                    object1.addProperty("quantidade", subPratoQuantIng);//adiciona a quantidade
                                    jsonArray.add(object1);//adiciona ao array de json
                                }
                            } else {
                                JsonObject object = new JsonObject();
                                String ingrediente = pratoPedIngs[f];
                                double quantIng = (Double.parseDouble(pratoPedQtds[f]) * Integer.parseInt(pratosPed.get(i).getQuant())) * posNeg;

                                object.addProperty("ingrediente", ingrediente);
                                object.addProperty("quantidade", quantIng);
                                jsonArray.add(object);
                            }
                        }
                    }
                }


                //Agora deve tratar os adicionais
                if (i < adRePed.size()) {
                    if (adRePed.get(i).getAdicional() != null) {
                        String pratoPedAds[] = adRePed.get(i).getAdicional().split(",");
                        if (pratoPedAds[0].length() > 0) {
                            for (String pedAds : pratoPedAds) {
                                JsonObject object = new JsonObject();
                                Ingredientes ing = controller.listarIngredientesNome(pedAds,0).get(0);

                                object.addProperty("ingrediente", ing.getNome());
                                object.addProperty("quantidade", (ing.getMedida_ad() * Double.parseDouble(adRePed.get(i).getQuant_adicional())) * posNeg);
                                jsonArray.add(object);
                            }
                        }
                    }


                    if (adRePed.get(i).getRetirada() != null) {
                        //Agora se trata as retiradas do prato
                        String pratoPedRes[] = adRePed.get(i).getRetirada().split(",");
                        String pratoPedIngs[] = pratoPed.get(0).getIngredientes().split(",");
                        String pratoPedQtds[] = pratoPed.get(0).getQuant_ing().split(",");

                        if (pratoPedRes[0].length() > 0) {
                            for (String pedRes : pratoPedRes) {//Roda as retiradas
                                for (int j = 0; j < pratoPedIngs.length; j++) {//Roda os ingredientes do prato, não retira ingredientes que não existam no prato
                                    if (pedRes.equals(pratoPedIngs[j])) {
                                        JsonObject object = new JsonObject();
                                        double quant = Double.parseDouble(pratoPedQtds[j]) * Integer.parseInt(adRePed.get(i).getQuant_retirada()) * posNeg;
                                        object.addProperty("ingrediente", pedRes);
                                        object.addProperty("quantidade", quant);
                                        jsonArray.add(object);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void deduzArrayJsonBebidas (ArrayList<Bebidas> bebidasPed) {

        if (bebidasPed.size()>0) {

            posNeg = 1;
            for (int i = 0; i < bebidasPed.size(); i++) {
                if (!bebidasPed.get(i).getNome().equals("")) {
                    ArrayList<Bebidas> bebida = controller.listarBebidasNome(bebidasPed.get(i).getNome());
                    if (bebida.size() == 1) {
                        JsonObject object = new JsonObject();
                        int quantBeb = Integer.parseInt(bebidasPed.get(i).getQuant()) * posNeg;

                        object.addProperty("bebida", bebidasPed.get(i).getNome());
                        object.addProperty("quantidade", quantBeb);
                        jsonArrayBeb.add(object);
                    }
                }
            }
        }
    }
}
