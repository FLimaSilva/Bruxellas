package com.codecorp.felipelima.bruxellas.view;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.fragments.InfoCozinhaFragment;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;

public class InfoCozinhaActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    InfoCozinhaFragment infoCozinhaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_cozinha);

        frameLayout = findViewById(R.id.frameInfo);

        UtilRestaurante.configuraStatusBar(InfoCozinhaActivity.this,getSupportActionBar(),"Informações cozinha");

        infoCozinhaFragment = new InfoCozinhaFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(),infoCozinhaFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
