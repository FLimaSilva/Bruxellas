package com.codecorp.felipelima.bruxellas.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Usuario;
import com.codecorp.felipelima.bruxellas.util.LoginAsyncTask;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {

    EditText editNome, editSenha;
    TextView texEsqSenha;
    Button buttonLogin;
    RestauranteController controller;
    Switch switchLembrar;
    Context context;
    private String PREFERENCES = "preferences_user";
    private String REMEMBER_NIVEL_USER = "user_remember";
    private String REMEMBER_NOME_USER = "user_name_remember";
    boolean auxiliar=false;

    private javax.mail.Session session;
    String emailSender = "felipe.lima.silva@live.com";
    String senhaSender = "fe47474108";

    private ProgressDialog pdialog = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = findViewById(R.id.buttonLogin);
        editNome = findViewById(R.id.editUsuario);
        editSenha = findViewById(R.id.editSenha);
        switchLembrar = findViewById(R.id.switchLembrar);
        texEsqSenha = findViewById(R.id.texEsqSenha);

        context = getApplicationContext();

        controller = new RestauranteController(context);
        pdialog = new ProgressDialog(this);

        final SharedPreferences preferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        if (preferences.contains(REMEMBER_NIVEL_USER)) {
            editor.remove(REMEMBER_NIVEL_USER);
            editor.remove(REMEMBER_NOME_USER);
            editor.commit();
        }

        editSenha.setOnTouchListener(new View.OnTouchListener() {

            private float touchX = 0;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                int drawableLeft = editSenha.getRight() - editSenha.getCompoundDrawables()[2].getBounds().width();

                if (event.getAction() == MotionEvent.ACTION_DOWN && event.getRawX() >= drawableLeft){
                    touchX = event.getRawX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP && touchX >= drawableLeft){
                    //UtilRestaurante.showMensagem(getApplicationContext(),"Clicked Button");
                    if (auxiliar) {
                        auxiliar = false;
                        editSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        editSenha.setSelection(editSenha.getText().length());
                    } else {
                        auxiliar = true;
                        editSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        editSenha.setSelection(editSenha.getText().length());
                    }
                    touchX=0;
                    return true;
                } else {
                    return editSenha.onTouchEvent(event);
                }
            }
        });

        getSupportActionBar().hide();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editNome.length()>0 && editSenha.length()>0){

                    LoginAsyncTask login = new LoginAsyncTask(LoginActivity.this, editNome.getText().toString(), editSenha.getText().toString(),
                            new LoginAsyncTask.AsynResponse() {
                                @Override
                                public void processFinish(boolean output) {
                                    if (output){
                                        Intent it;
                                        String nivel = preferences.getString(REMEMBER_NIVEL_USER,"");
                                        switch(nivel){
                                                case "adm":
                                                    it = new Intent(LoginActivity.this, MainActivity.class);
                                                    it.putExtra("firstLoad", 1);
                                                    startActivity(it);
                                                    finish();
                                                    break;
                                                case "gar":
                                                    it = new Intent(LoginActivity.this, UsuarioGarcomActivity.class);
                                                    startActivity(it);
                                                    finish();
                                                    break;
                                                case "coz":
                                                    it = new Intent(LoginActivity.this, UsuarioCozinhaActivity.class);
                                                    startActivity(it);
                                                    finish();
                                                    break;
                                        }

                                        if (!switchLembrar.isChecked()){
                                            editor.remove(REMEMBER_NIVEL_USER);
                                            editor.commit();
                                        }
                                    }
                                }
                            });
                    login.execute();
                } else {
                    UtilRestaurante.showMensagem(getApplicationContext(), "Por favor digite um usuário e senha válidos!!");
                }
            }
        });

        texEsqSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((editNome.length() <= 0) || (controller.listarUsuariosNome(editNome.getText().toString()).size() <= 0)){
                    UtilRestaurante.showMensagem(getApplicationContext(),"Insira um nome de usuário válido, por favor");

                } else {// caso esteja tudo certo com o email inserido pelo usuário
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Properties properties = new Properties();

                    //properties.put("mail.smtp.host","smtp.googlemail.com");
                    //properties.put("mail.smtp.socketFactory.port","465");
                    //properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                    //properties.put("mail.smtp.auth","true");
                    //properties.put("mail.smtp.port","465"); //Envia de email gmail

                    properties.put("mail.smtp.host","smtp.live.com");
                    properties.put("mail.smtp.auth","true");
                    properties.put("mail.smtp.starttls.enable","true"); //Envia email de outlook*/

                    session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                        @Override
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new javax.mail.PasswordAuthentication(emailSender,senhaSender);
                        }
                    });

                    pdialog.setMessage("Enviando e-mail");
                    pdialog.setCancelable(false);
                    pdialog.show();

                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute();
                }
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (session != null){
                    javax.mail.Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailSender));
                    Usuario userLog = controller.listarUsuariosNome(editNome.getText().toString()).get(0);
                    message.setSubject("Bruxellas Sistema - Esqueci minha senha - Usuario: "+userLog.getNome());//colocar o nome da pessoa
                    message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(userLog.getEmail()));
                    String assunto = "Caro senhor(a),"+"\n\n" +
                            "" +
                            "A sua senha para o sistema em questão é: "+userLog.getSenha()+".\n\n" +
                            "" +
                            "A equipe bruxellas agradece!";
                    message.setContent(assunto,"text/html; charset=utf-8");
                    //Transport.send(message);//apenas de email enviado pelo gmail

                    Transport transport = session.getTransport("smtp");
                    transport.connect("smtp.live.com",587,emailSender,senhaSender);
                    transport.sendMessage(message,message.getAllRecipients());
                    transport.close();//apenas de email enviado pelo outlook*/
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pdialog.dismiss();
            UtilRestaurante.showMensagem(context,"Email enviado com sucesso!");
        }
    }

}
