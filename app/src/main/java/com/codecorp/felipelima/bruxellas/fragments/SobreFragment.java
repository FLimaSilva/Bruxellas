package com.codecorp.felipelima.bruxellas.fragments;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.view.MainActivity;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * A simple {@link Fragment} subclass.
 */
public class SobreFragment extends Fragment {


    public SobreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.fab.setVisibility(View.GONE);

        final Activity a = getActivity();
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_sobre,container,false);
        Element versionElement = new Element();
        versionElement.setTitle("Version 6.2");

        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        String descricao = "A CodeCorp é uma empresa de desenvolvimento de sistemas, atualmente focada no comércio, proporcionando agilidade e facilidades com suas aplicações.\n\n" +
                "Ajudar as pessoas a atingir o máximo potencial de seus respectivos empreendimentos, seja ele de qualquer porte.Queremos ver nossos clientes felizes em ter uma solução nossa, por ela trazer a oportunidade de tirarem o máximo de seus negócios.";

        View sobre = new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.drawable.codecorp_marca1_redimensionada)
                .setDescription(descricao)
                .addGroup("Fale conosco")
                .addEmail("felipe.lima@codecorp.com.br","Envie um e-mail!")
                .addWebsite("http://codecorp.com.br","Acesse nosso site!")

                .addGroup("Acesse nossas redes sociais")
                .addFacebook("codecorp","Facebook")
                //.addTwitter("google","Twitter")
                //.addYoutube("google","Youtube")
                //.addPlayStore("com.google.android.apps.plus","PlayStore")
                //.addGitHub("google","Github")
                //.addInstagram("google","Instagram")

                .create();

        viewGroup.addView(sobre);

        return viewGroup;
    }

}
