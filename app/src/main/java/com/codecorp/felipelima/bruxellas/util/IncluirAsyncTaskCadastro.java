package com.codecorp.felipelima.bruxellas.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.datamodel.RestauranteDataModel;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Usuario;

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

public class IncluirAsyncTaskCadastro extends AsyncTask<String, String, String>{

    private ProgressDialog progressDialog;

    private HttpURLConnection conn;
    private URL url = null;
    private Uri.Builder builder;

    Context context;

    public IncluirAsyncTaskCadastro(Usuario usuario, Context context){

        this.builder = new Uri.Builder();
        this.context = context;

        builder.appendQueryParameter("app","bruxellas");

        builder.appendQueryParameter(RestauranteDataModel.getUsuario_nome(),usuario.getNome());
        builder.appendQueryParameter(RestauranteDataModel.getUsuario_nivel(),usuario.getNivel());
        builder.appendQueryParameter(RestauranteDataModel.getUsuario_senha(),usuario.getSenha());

    }

    @Override
    protected void onPreExecute(){
        Log.i("WebService", "IncluirAsyncTaskCadastro()");

        progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Salvando o cadastro no banco de dados, por favor aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {//faz de fato a sincronização com o webservice

        //Montar a URL com o endereço do script PHP

        try {
            url = new URL(UtilRestaurante.URL_WEB_SERVICE+"APIIncluirDados_usuarios.php");
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
    protected void onPostExecute(String result){

        if (result.contains("timed out")){
            UtilRestaurante.showMensagem(context,"Falha para se conectar ao banco de dados!!\n\nPor favor, verifique se o Wi-Fi do celular está ligado.\nOu se o servidor está rodando. ");
        }

        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }

    }
}
