package com.codecorp.felipelima.bruxellas.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.codecorp.felipelima.bruxellas.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UtilRestaurante {

    // URL do servido Apache
    // Informe o endereço de IP se estiver rodando em seu computador
    // Informe o URL real se estiver rodadno em uma hospedagem
    //public static final String URL_WEB_SERVICE = "http://192.168.1.100:8090/bruxellas/";//IP casa
    //public static final String URL_WEB_SERVICE = "http://192.168.1.32:8090/bruxellas/";
    public static final String URL_WEB_SERVICE = "http://192.168.0.10:8090/bruxellas/";//IP bruxellas
    //public static final String URL_WEB_SERVICE = "http://192.168.0.120:8090/bruxellas/";//IP Walisson

    // Tempo máximo para considerar TIMEOUT para conectar ao Apache
    public static final int CONNECTION_TIMEOUT = 10000; //10 segundos

    // Tempo máximo para considerar erro de resposta do Apache
    public static final int READ_TIMEOUT = 15000; // 15 segundos

    public static String formatarValorDecimal (Double valor){
        BigDecimal valorDecString = new BigDecimal(valor).setScale(2, RoundingMode.HALF_DOWN);
        return String.valueOf(valorDecString);
    }

    public static String formatarValorDecimal (String valor){
        BigDecimal valorDecString = new BigDecimal(Double.parseDouble(valor)).setScale(2, RoundingMode.HALF_DOWN);
        return String.valueOf(valorDecString);
    }

    public static void showMensagem (Context context, String mensagem){
        Toast.makeText(context,mensagem,Toast.LENGTH_LONG).show();
    }

    public static void configuraStatusBar(Activity activity, ActionBar actionBar,String title){

        actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //Verifica a compatibilidade da função que será utilizada
            //Código milagroso que coloca cor na status bar
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
        else {
            UtilRestaurante.showMensagem(activity.getApplicationContext(),"Incompatibilidade com este dispositivo!");
        }
    }

    public static void configuraStatusBar(Activity activity, ActionBar actionBar,String title, boolean home){

        actionBar.setElevation(0);
        if (home) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        actionBar.setTitle(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //Verifica a compatibilidade da função que será utilizada
            //Código milagroso que coloca cor na status bar
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
        else {
            UtilRestaurante.showMensagem(activity.getApplicationContext(),"Incompatibilidade com este dispositivo!");
        }
    }
}
