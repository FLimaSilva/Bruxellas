package com.codecorp.felipelima.bruxellas.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.codecorp.felipelima.bruxellas.R;
import com.codecorp.felipelima.bruxellas.adapter.EstoqueAdapter;
import com.codecorp.felipelima.bruxellas.controller.RestauranteController;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EstoqueFragment extends Fragment {

    private View view;
    private RestauranteController controller;
    private ListView listView;
    private EstoqueAdapter estoque;
    private EditText editTextFiltroIngEtq;

    ArrayList<Ingredientes> ingredientes;

    public EstoqueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_estoque, container, false);

        final Activity a = getActivity();
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView = view.findViewById(R.id.listViewEstoque);
        editTextFiltroIngEtq = view.findViewById(R.id.editTextFiltroIngEtq);

        controller = new RestauranteController(getContext());

        ingredientes = controller.listarIngredientes();

        estoque = new EstoqueAdapter(getContext(),ingredientes);
        listView.setAdapter(estoque);

        editTextFiltroIngEtq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredientes = controller.listarIngredientesNome(charSequence.toString(),1);

                estoque = new EstoqueAdapter(getContext(),ingredientes);
                listView.setAdapter(estoque);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    /*@TargetApi(Build.VERSION_CODES.KITKAT)
    public void createPDF(ArrayList<Ingredientes> ingsPDF){

        PrintAttributes printAttrs = new PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .setResolution(new PrintAttributes.Resolution("zooey",getContext().PRINT_SERVICE,300,300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

        PrintedPdfDocument doc = new PrintedPdfDocument(getContext(),printAttrs);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,300,1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);

        View content = getView().findViewById(R.id.editTextFiltroIngEtq);
        content.draw(page.getCanvas());

        doc.finishPage(page);

        try {
            File pdfDirPath = new File(getContext().getFilesDir(), "pdfs");
            pdfDirPath.mkdirs();
            File file = new File(pdfDirPath, "pdfsend.pdf");
            Uri contentUri = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", file);
            FileOutputStream os = new FileOutputStream(file);
            doc.writeTo(os);
            doc.close();
            os.close();
            //shareDocument(contentUri);
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }*/

}
