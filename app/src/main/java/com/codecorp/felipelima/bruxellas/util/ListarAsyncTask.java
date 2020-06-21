package com.codecorp.felipelima.bruxellas.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.datamodel.RestauranteDataModel;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.model.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
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

public class ListarAsyncTask extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private RestauranteController controller;
    Context context;

    private HttpURLConnection conn;
    private URL url = null;
    private Uri.Builder builder;
    private String tabela;
    AsynResponse asynResponse = null;

    public ListarAsyncTask(String tabela, Context context, boolean progress, AsynResponse asynResponse){

        if (progress) {
            progressDialog = new ProgressDialog(context);
        }
        controller = new RestauranteController(context);
        this.context = context;
        this.tabela = tabela;
        this.asynResponse = asynResponse;

        this.builder = new Uri.Builder();
        builder.appendQueryParameter("app","bruxellas");
    }

    public ListarAsyncTask(String tabela, Context context){

        controller = new RestauranteController(context);
        this.context = context;
        this.tabela = tabela;

        this.builder = new Uri.Builder();
        builder.appendQueryParameter("app","bruxellas");
    }

    public interface AsynResponse{
        void processFinish(boolean output);
    }

    @Override
    protected void onPreExecute(){//ativa notificação para o usuário
        Log.i("WebService","SincronizarSistema()");
        if (progressDialog != null) {
            progressDialog.setMessage("Sincronizando o banco de dados, por favor aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        //faz de fato a sincronização com o webservice
        //Montar a URL com o endereço do script PHP

        try {
            if (tabela.equals("usuarios")){
                url = new URL(UtilRestaurante.URL_WEB_SERVICE+"APISincronizarSistema_"+ tabela +"_new2.php");
            } else {
                url = new URL(UtilRestaurante.URL_WEB_SERVICE+"APISincronizarSistema_"+ tabela +".php");
            }
        } catch (MalformedURLException e){
            Log.e("WebService","MalFormedURLException - "+e.getMessage());
        } catch (Exception e) {
            Log.e("WebService", "Exception - " + e.getMessage());
        }

        // faz a conexão com o servidor apache
        try {

            // pega a url que foi criada anteriormente e insere dentro da estrutura de conexão
            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(UtilRestaurante.CONNECTION_TIMEOUT); // configura o timeout da conexão com o apache
            conn.setReadTimeout(UtilRestaurante.READ_TIMEOUT); //configura o timeout da resposta com os dados do webservice

            conn.setRequestMethod("POST");//informa qual é o método para efetuar a conexão http

            conn.setRequestProperty("charset","utf-8");//informa qual é a condificação para essa conexão http

            conn.setDoInput(true);//informa que haverá a necessidade de receber dados
            conn.setDoOutput(true);//informa que haverá a necessidade de salvar dados

            conn.connect();//abre a conexão

            //cria uma string com os parâmetros que identificam que é o app "correto" que faz a solicitação de maneira codificada
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();

            os.close();
            //verificar o vídeo novamente para entender melhor essa parte do código
            conn.connect();

        } catch (IOException e){
            Log.e("WebService", "IOException - "+e.getMessage());
        }

        // verifica a resposta da conexão com WebService
        try {
            int response_code = conn.getResponseCode();

            //200 Ok
            //403 Forbbiden
            //404 pág não encontrada
            //503 erro interno no servido

            if (response_code == HttpURLConnection.HTTP_OK){//verifica se a resposta foi ok (200)

                //serve para pegar todos os dados que vieram na resposta do WebService
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                //cria estrutura para toda a reposta inteira enviada pelo WebService
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null){
                    result.append(line);
                }

                // retorna todos os dados
                return (result.toString());
            } else{
                return ("Erro de conexão");
            }

        } catch (IOException e){
            Log.e("WebService", "IOException - "+e.getMessage());
            return e.toString();
        } finally {
            conn.disconnect();
        }

    }

    @Override
    protected void onPostExecute(String result){//desativa notificação para usuário e salvar no banco de dados os dados passados pelo servidor

        try{
            JSONArray jsonArray;

            if (result.contains("id_prato") || result.contains("id_bebida") || result.contains("id_ingrediente") || result.contains("id_pedido") || result.contains("id_pessoa")) {
                jsonArray = new JSONArray(result);
            } else if (result.contains("false")) {
                jsonArray = new JSONArray();
                //UtilRestaurante.showMensagem(context,"A tabela de "+tabela+" está vazia");
            } else {
                jsonArray = new JSONArray();
                UtilRestaurante.showMensagem(context, "Falha para se conectar ao banco de dados!!\n\nPor favor, verifique se o Wi-Fi do celular está ligado. E se o servidor está rodando. ");
            }

            switch (tabela) {
                case "pratos":
                    convertePratos(jsonArray);
                    break;

                case "ingredientes":
                    converteIngredientes(jsonArray);
                    break;

                case "bebidas":
                    converteBebidas(jsonArray);
                    break;

                case "pedido":
                    convertePedido(jsonArray);
                    break;

                case "usuarios":
                    converteUsuario(jsonArray);
                    break;
            }

        } catch (JSONException e){
            Log.e("WebService", "Erro JSONException - " + e.getMessage());
        } catch (Exception e){
            Log.e("WebService", "Erro Exception - " + e.getMessage());
        } finally {
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if (asynResponse != null) {
                asynResponse.processFinish(true);
            }
        }
    }

    private void convertePratos(JSONArray jsonArray) throws JSONException {

        controller.deletarTabela(RestauranteDataModel.getPrato_tabela());//Apaga a tabela existente
        controller.criarTabela(RestauranteDataModel.criarTabelaPrato());//Cria uma nova tabela

        if (jsonArray.length() != 0){
            //Salvar os dados no banco de dados SQLite

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i); //pego um item do array do json

                Pratos pratos = new Pratos();//crio a estrutura que eu quero popular

                pratos.setId(object.getInt(RestauranteDataModel.getPrato_id()));
                pratos.setNome(object.getString(RestauranteDataModel.getPrato_nome()));
                pratos.setTipo(object.getString(RestauranteDataModel.getPrato_tipo()));
                pratos.setSub_prato(object.getInt(RestauranteDataModel.getPrato_subprato()));
                pratos.setIngredientes(object.getString(RestauranteDataModel.getPrato_ingredientes()));
                pratos.setQuant_ing(object.getString(RestauranteDataModel.getPrato_quant_ing()));
                pratos.setIng_quant(object.getString(RestauranteDataModel.getPrato_ing_quant()));
                pratos.setPreco_custo(object.getString(RestauranteDataModel.getPrato_preco_custo()));
                pratos.setPreco_venda(object.getString(RestauranteDataModel.getPrato_preco_venda()));

                controller.salvarPratos(pratos);
            }
        }
    }

    private void converteBebidas(JSONArray jsonArray) throws JSONException {

        controller.deletarTabela(RestauranteDataModel.getBebida_tabela());//Apaga a tabela existente
        controller.criarTabela(RestauranteDataModel.criarTabelaBebida());//Cria uma nova tabela

        if (jsonArray.length() != 0){
            //Salvar os dados no banco de dados SQLite

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i); //pego um item do array do json

                Bebidas bebidas = new Bebidas();//crio a estrutura que eu quero popular

                bebidas.setId(object.getInt(RestauranteDataModel.getBebida_id()));
                bebidas.setNome(object.getString(RestauranteDataModel.getBebida_nome()));
                bebidas.setPreco_custo(object.getString(RestauranteDataModel.getBebida_preco_custo()));
                bebidas.setPreco_venda(object.getString(RestauranteDataModel.getBebida_preco_venda()));
                bebidas.setQuant_atual(object.getString(RestauranteDataModel.getBebida_quant_atual()));
                bebidas.setQuant_min(object.getString(RestauranteDataModel.getBebida_quant_min()));

                controller.salvarBebidas(bebidas);
            }
        }
    }

    private void converteIngredientes(JSONArray jsonArray) throws JSONException {

        controller.deletarTabela(RestauranteDataModel.getIngrediente_tabela());//Apaga a tabela existente
        controller.criarTabela(RestauranteDataModel.criarTabelaIngrediente());//Cria uma nova tabela

        if (jsonArray.length() != 0){
            //Salvar os dados no banco de dados SQLite

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i); //pego um item do array do json

                Ingredientes ingredientes = new Ingredientes();//crio a estrutura que eu quero popular

                ingredientes.setId(object.getInt(RestauranteDataModel.getIngrediente_id()));
                ingredientes.setNome(object.getString(RestauranteDataModel.getIngrediente_nome()));
                ingredientes.setTipo(object.getString(RestauranteDataModel.getIngrediente_tipo()));
                ingredientes.setUnidade(object.getString(RestauranteDataModel.getIngrediente_unidade()));
                ingredientes.setReferencia(object.getString(RestauranteDataModel.getIngrediente_referencia()));
                ingredientes.setMedida(object.getInt(RestauranteDataModel.getIngrediente_medida()));
                ingredientes.setQuant(object.getDouble(RestauranteDataModel.getIngrediente_quant()));
                ingredientes.setQuant_min(object.getDouble(RestauranteDataModel.getIngrediente_quant_min()));
                ingredientes.setControle(object.getInt(RestauranteDataModel.getIngrediente_controle()));
                ingredientes.setMedida_ad(object.getDouble(RestauranteDataModel.getIngrediente_medida_ad()));
                ingredientes.setCusto_uni(object.getString(RestauranteDataModel.getIngrediente_custo_un()));
                ingredientes.setCusto_ing(object.getString(RestauranteDataModel.getIngrediente_custo_ing()));
                //ingredientes.setPreco_ing(object.getString(RestauranteDataModel.getIngrediente_preco_ing()));
                ingredientes.setPreco_ad(object.getString(RestauranteDataModel.getIngrediente_preco_ad()));

                controller.salvarIngredientes(ingredientes);
            }
        }
    }

    private void convertePedido(JSONArray jsonArray) throws JSONException {

        controller.deletarTabela(RestauranteDataModel.getPedido_tabela());//Apaga a tabela existente
        controller.criarTabela(RestauranteDataModel.criarTabelaPedido());//Cria uma nova tabela

        if (jsonArray.length() != 0){
            //Salvar os dados no banco de dados SQLite

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i); //pego um item do array do json

                Pedidos pedidos = new Pedidos();//crio a estrutura que eu quero popular

                pedidos.setIdPK(object.getInt(RestauranteDataModel.getPedido_id()));
                pedidos.setMesa(object.getInt(RestauranteDataModel.getPedido_mesa()));
                pedidos.setQuant_pessoas(object.getString(RestauranteDataModel.getPedido_quant_pessoas()));
                pedidos.setNome(object.getString(RestauranteDataModel.getPedido_nome()));
                pedidos.setNome_usuario(object.getString(RestauranteDataModel.getPedido_nome_usuario()));
                pedidos.setPrato(object.getString(RestauranteDataModel.getPedido_prato()));
                pedidos.setQuant_prato(object.getString(RestauranteDataModel.getPedido_quant_prato()));
                pedidos.setCondicao_prato(object.getInt(RestauranteDataModel.getPedido_condicao_prato()));
                pedidos.setAdicional(object.getString(RestauranteDataModel.getPedido_adicional()));
                pedidos.setRetirar(object.getString(RestauranteDataModel.getPedido_retirar()));
                pedidos.setBebida(object.getString(RestauranteDataModel.getPedido_bebida()));
                pedidos.setQuant_bebida(object.getString(RestauranteDataModel.getPedido_quant_bebida()));
                pedidos.setPreco(object.getString(RestauranteDataModel.getPedido_preco()));
                pedidos.setObs(object.getString(RestauranteDataModel.getPedido_obs()));
                pedidos.setData_hora(object.getString(RestauranteDataModel.getPedido_data_hora()));
                pedidos.setStatus(object.getString(RestauranteDataModel.getPedido_status()));

                controller.salvarPedido(pedidos);
            }
        } else {
            UtilRestaurante.showMensagem(context,"Nenhum pedido foi encontrado no momento!!");
        }

    }

    private void converteUsuario(JSONArray jsonArray) throws JSONException {

        controller.deletarTabela(RestauranteDataModel.getUsuario_tabela());//Apaga a tabela existente
        controller.criarTabela(RestauranteDataModel.criarTabelaUsuario());//Cria uma nova tabela

        if (jsonArray.length() != 0){
            //Salvar os dados no banco de dados SQLite

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i); //pego um item do array do json

                Usuario usuario = new Usuario();//crio a estrutura que eu quero popular

                usuario.setId(object.getInt(RestauranteDataModel.getUsuario_id()));
                usuario.setNome(object.getString(RestauranteDataModel.getUsuario_nome()));
                usuario.setEmail(object.getString(RestauranteDataModel.getUsuario_email()));
                usuario.setNivel(object.getString(RestauranteDataModel.getUsuario_nivel()));
                usuario.setSenha(object.getString(RestauranteDataModel.getUsuario_senha()));

                controller.salvarUsuarios(usuario);
            }
        }
    }

}