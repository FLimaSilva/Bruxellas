package com.codecorp.felipelima.bruxellas.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.AdicionalRetiradaPedido;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.ArrayList;

public class DeletarPedidoAsyncTask extends AsyncTask<String, String, String> {

    Context context;
    private ArrayList<Pedidos> pedidos;
    private ArrayList<Pedidos> pedAltera;
    private AsyncResponse asyncResponse;
    private ProgressDialog progressDialog;
    private TinyDB tinyDB;
    private RestauranteController controller;
    private String funcao;
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

    public DeletarPedidoAsyncTask(Context context, ArrayList<Pedidos> pedidos, String funcao, ArrayList<Pedidos> pedAltera, AsyncResponse asyncResponse) {
        this.context = context;
        this.pedidos = pedidos;
        this.pedAltera = pedAltera;
        this.asyncResponse = asyncResponse;
        this.funcao = funcao;

        pratosPedidos = new ArrayList<>();
        adRePedidos = new ArrayList<>();
        bebidasPedidos = new ArrayList<>();

        controller = new RestauranteController(context);
        progressDialog = new ProgressDialog(context);
        tinyDB = new TinyDB(context);
    }

    public interface AsyncResponse {
        void processFinish(boolean status);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setCancelable(false);
        if (!funcao.contains("id")) {
            progressDialog.setTitle("Deletando pedidos");
            progressDialog.setMessage("Deletando o pedido no banco de dados");
        } else {
            progressDialog.setTitle("Alterando pedidos");
            progressDialog.setMessage("Alterando o pedido no banco de dados");
        }
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        boolean sucess = true;
        boolean auxEstoque = false;

        try {

            switch (funcao) {//Primeira coisa a se fazer é deletar os pedidos dos banco de dados, de acordo com o tipo de delete

                case "id-altera"://Só preciso fazer a adequação neste item, pois é só ele que é chamado no altera pedido
                    auxEstoque = true;

                    for (Pedidos pedAlter:pedAltera) {//altera todos os pedidos que já existem
                        sucess = sucess && controller.alterarPedido(pedAlter);//salva no Sqlite

                        if (sucess){
                            AlterarAsyncTask alterTask = new AlterarAsyncTask(pedAlter, context, false);
                            alterTask.execute();//Salva no webservice
                        }
                    }

                    for (Pedidos pedDel:pedidos) {
                            sucess = sucess && controller.deletarPedidoId(pedDel.getId());//deleta o pedido no banco, passando apenas o id**pode até mudar depois

                            if (sucess) {
                                DeletarAsyncTask task = new DeletarAsyncTask(pedDel, context);
                                task.execute(); //Deleta todos os pedidos do MySQL
                            }
                    }
                    break;

                case "todos":
                    //Primeira coisa a se fazer é deletar os pedidos dos banco de dados
                    sucess = sucess && controller.deletarPedidoMesa(String.valueOf(pedidos.get(0).getMesa()));//deleta do SQLite

                    if (sucess) {//Caso tenha sido deletado com sucesso
                        for (Pedidos pedDel : pedidos) {
                            DeletarAsyncTask task = new DeletarAsyncTask(pedDel, context);
                            task.execute(); //Deleta todos os pedidos do MySQL
                        }
                    }
                    break;

                case "nome":
                    sucess = sucess && controller.deletarPedidoNome(String.valueOf(pedidos.get(0).getMesa()), pedidos.get(0).getNome());

                    if (sucess){
                        for (Pedidos pedDel : pedidos) {
                            DeletarAsyncTask task = new DeletarAsyncTask(pedDel, context);
                            task.execute(); //Deleta todos os pedidos do MySQL
                        }
                    }
                    break;
            }

            //Agora tem que pegar todos os ingredientes e adicionar dentro do arquivo do json
            Thread.sleep(200);
            publishProgress("Separando os ingredientes dos pratos para o estoque");

            if (!auxEstoque) {//se não for true significa que deleta tudo
                for (Pedidos ped : pedidos) {
                    boolean auxAdRe = false;
                    int quantPrato = 0;
                    int quantBebida = 0;

                    if (ped.getQuant_prato().length()>0) {
                        quantPrato = Integer.parseInt(ped.getQuant_prato());
                    }
                    if (ped.getQuant_bebida().length()>0) {
                        quantBebida = Integer.parseInt(ped.getQuant_bebida());
                    }

                    Pratos prato = new Pratos();
                    AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();
                    Bebidas bebida = new Bebidas();


                    if (!ped.getPrato().equals("")){
                        prato.setNome(ped.getPrato());
                        prato.setQuant(String.valueOf(quantPrato*-1));
                        pratosPedidos.add(prato);
                    }

                    if (!ped.getAdicional().equals("")){
                        adRe.setAdicional(ped.getAdicional());
                        adRe.setQuant_adicional(String.valueOf(quantPrato*-1));
                        auxAdRe = true;
                    }

                    if (!ped.getRetirar().equals("")){
                        adRe.setRetirada(ped.getRetirar());
                        adRe.setQuant_retirada(String.valueOf(quantPrato));
                        auxAdRe = true;
                    }

                    if (!ped.getBebida().equals("")){
                        bebida.setNome(ped.getBebida());
                        bebida.setQuant(String.valueOf(quantBebida*-1));
                        bebidasPedidos.add(bebida);
                    }

                    //if (auxAdRe){
                        adRePedidos.add(adRe);
                    //}
                }
                deduzArrayJsonPratos(pratosPedidos,adRePedidos);
                deduzArrayJsonBebidas(bebidasPedidos);

            } else {//significa que os ingredientes a serem subtraídos estão no tinyDb

                String listasPratos[] = {LISTA_ESTOQUE,LISTA_ESTOQUE_DEL};
                String listasBebidas[] = {LISTA_ESTOQUE_BEBIDAS,LISTA_ESTOQUE_BEBIDAS_DEL};

                for (int j = 0; j < listasPratos.length; j++) {

                    String listaPrato = listasPratos[j];
                    if (tinyDB.containsKey(listaPrato)) {// pega os pratos e ingredientes que estão no shared preferences para pratos e adRe para ser deletados
                        JsonArray pratosPed = tinyDB.getListEstoque(listaPrato);
                        adRePedidos = new ArrayList<>();
                        pratosPedidos = new ArrayList<>();

                        for (int i = 0; i < pratosPed.size(); i++) {
                            boolean auxAdRe = false;

                            JsonObject object = pratosPed.get(i).getAsJsonObject();
                            Pratos prato = new Pratos();
                            AdicionalRetiradaPedido adRe = new AdicionalRetiradaPedido();

                            prato.setNome(object.get("prato").getAsString());
                            prato.setQuant(object.get("quantidade").getAsString());

                            if (object.has("adicional")) {
                                adRe.setAdicional(object.get("adicional").getAsString());
                                adRe.setQuant_adicional(object.get("quantidade").getAsString());
                                auxAdRe = true;
                            }

                            if (object.has("retirada")) {
                                adRe.setRetirada(object.get("retirada").getAsString());
                                adRe.setQuant_retirada(object.get("quantidadeRe").getAsString());
                                auxAdRe = true;
                            }

                            //if (auxAdRe) {
                            adRePedidos.add(adRe);
                            //}
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
            }

            Thread.sleep(200);
            publishProgress("Atualizando os ingredientes no estoque");

            AtualizarEstoqueAsyncTask atualizaTask = new AtualizarEstoqueAsyncTask(jsonArray,jsonArrayBeb,context);
            atualizaTask.execute();

            Thread.sleep(200);
            publishProgress("Atualizando a lista de pedidos");

            ListarAsyncTask taskListar = new ListarAsyncTask("pedido",context);
            taskListar.execute();

        } catch (Exception e){
            Log.e("Exception delete", "Message: "+e.getMessage());
        }

        Log.i("QuantIngs", "Del-JSON: "+jsonArray.toString());
        Log.i("QuantIngs", "Del-JSONB: "+jsonArrayBeb.toString());

        return String.valueOf(sucess);
    }

    @Override
    protected void onPostExecute(String s) {

        if (!funcao.contains("id")) {
            progressDialog.setMessage("Pedido realizado com sucesso");
        } else {
            progressDialog.setMessage("Pedido alterado com sucesso");
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

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
        super.onProgressUpdate(values);
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