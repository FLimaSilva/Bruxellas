package com.codecorp.felipelima.bruxellas.fragments;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Usuario;
import com.codecorp.felipelima.bruxellas.util.IncluirAsyncTaskCadastro;
import com.codecorp.felipelima.bruxellas.util.UtilRestaurante;
import com.codecorp.felipelima.bruxellas.view.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CadastroFragment extends Fragment {

    private View view;
    EditText editNome, editSenha;
    Button buttonSalvar;
    RestauranteController controller;
    Spinner spinnerNivelUsuario;
    String[] niveisBd = {"adm","gar","coz"};

    public CadastroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cadastro, container, false);

        buttonSalvar = view.findViewById(R.id.buttonSalvar);
        editNome = view.findViewById(R.id.editNewUsuario);
        editSenha = view.findViewById(R.id.editNewSenha);

        controller = new RestauranteController(getContext());

        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.fab.setVisibility(View.GONE);

        final Activity a = getActivity();
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        spinnerNivelUsuario = view.findViewById(R.id.spinnerNivelAcesso);
        String[] niveis = getResources().getStringArray(R.array.niveis_acesso);

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,niveis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelUsuario.setAdapter(adapter);

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNome.getText().toString().length()>3 && editSenha.getText().toString().length()>3){

                    Usuario usuario = new Usuario();
                    usuario.setNome(editNome.getText().toString());
                    usuario.setSenha(editSenha.getText().toString());
                    usuario.setNivel(niveisBd[spinnerNivelUsuario.getSelectedItemPosition()]);

                    if (controller.salvarUsuarios(usuario)){
                        IncluirAsyncTaskCadastro taskCadastro = new IncluirAsyncTaskCadastro(usuario,getContext());
                        taskCadastro.execute();

                        UtilRestaurante.showMensagem(getContext(),"O usuário: "+editNome.getText().toString() + " e a senha: "+editSenha.getText().toString() +"\n" +
                                "Foram salvos com sucesso!!");

                        editNome.setText("");
                        editSenha.setText("");
                    }

                } else {
                    UtilRestaurante.showMensagem(getContext(),"Digite um usuário/senha acima de 3 letras!");
                }
            }
        });

        return view;
    }

}
