package com.codecorp.felipelima.bruxellas.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Usuario;

import java.util.ArrayList;

public class LoginAsyncTask extends AsyncTask<String, String, String> {

    Context context;
    private ProgressDialog progress;
    private AsynResponse asynResponse;
    private String nome;
    private String senha;
    private RestauranteController controller;
    private ArrayList<Usuario> usuarios;
    private final SharedPreferences.Editor editor;

    private String PREFERENCES = "preferences_user";
    private String REMEMBER_NIVEL_USER = "user_remember";
    private String REMEMBER_NOME_USER = "user_name_remember";

    public LoginAsyncTask(Context context, String nome, String senha, AsynResponse asynResponse) {
        this.context = context;
        this.nome = nome;
        this.senha = senha;
        this.asynResponse = asynResponse;

        progress = new ProgressDialog(context);

        final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES,0);
        editor = preferences.edit();
    }

    public interface AsynResponse{
        void processFinish(boolean output);
    }

    @Override
    protected void onPreExecute() {
        progress.setTitle("Login");
        progress.setMessage("Efetuando o login, por favor aguarde...");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        controller = new RestauranteController(context);
    }

    @Override
    protected String doInBackground(String... strings) {

        int progress = 0;

        usuarios = controller.listarUsuariosNomeSenha(nome,senha);

        while (progress < 10){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress++;
        }

        return "DONE";
    }

    @Override
    protected void onPostExecute(String s) {
        progress.dismiss();

        if (usuarios.size() == 1) {

            switch (usuarios.get(0).getNivel()) {
                case "adm":
                    editor.putString(REMEMBER_NIVEL_USER, "adm");
                    editor.putString(REMEMBER_NOME_USER, usuarios.get(0).getNome());
                    break;

                case "gar":
                    editor.putString(REMEMBER_NIVEL_USER, "gar");
                    editor.putString(REMEMBER_NOME_USER, usuarios.get(0).getNome());
                    break;
                case "coz":
                    editor.putString(REMEMBER_NIVEL_USER, "coz");
                    editor.putString(REMEMBER_NOME_USER, usuarios.get(0).getNome());
                    break;
            }

            editor.commit();

        } else {
            UtilRestaurante.showMensagem(context, "O usuário/senha não estão cadastrados!!");
        }
        asynResponse.processFinish(true);
    }
}
