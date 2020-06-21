package com.codecorp.felipelima.bruxellas.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;

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

public class AtualizarEstoqueAsyncTask extends AsyncTask<String, String, String> {

    private HttpURLConnection conn;
    private URL url = null;
    private Uri.Builder builder;
    Context context;

    public AtualizarEstoqueAsyncTask(JsonArray jsonArray, JsonArray jsonArrayBeb, Context context) {
        this.builder = new Uri.Builder();
        this.context = context;

        builder.appendQueryParameter("app","bruxellas");
        builder.appendQueryParameter("jsonArray", jsonArray.toString());
        builder.appendQueryParameter("jsonArrayBeb", jsonArrayBeb.toString());
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... strings) {
        //Montar a URL com o endereço do script PHP

        try {
            url = new URL(UtilRestaurante.URL_WEB_SERVICE+"APIAtualizarDados_estoque.php");
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
    protected void onPostExecute(String result) {

        if (result.contains("timed out") || result.contains("Erro de conexão")){
            UtilRestaurante.showMensagem(context,"Falha para se conectar ao banco de dados!!\n\nPor favor, verifique se o Wi-Fi do celular está ligado.\nOu se o servidor está rodando. ");
        }

    }
}
