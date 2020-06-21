package com.codecorp.felipelima.bruxellas.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.datamodel.RestauranteDataModel;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.model.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class DataSource extends SQLiteOpenHelper{

    private static final String DB_NAME = "bruxellas.sqlite";
    private static final int DB_VERSION = 1;

    Cursor cursor;

    SQLiteDatabase db;

    public DataSource(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            db.execSQL(RestauranteDataModel.criarTabelaPrato());
            db.execSQL(RestauranteDataModel.criarTabelaBebida());
            db.execSQL(RestauranteDataModel.criarTabelaIngrediente());
            db.execSQL(RestauranteDataModel.criarTabelaPedido());
            db.execSQL(RestauranteDataModel.criarTabelaUsuario());

        } catch (Exception e){

            Log.e("Media", "DB---> ERRO: " + e.getMessage());

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        int ik =0;
    }

    public boolean insert(String tabela, ContentValues dados){

        boolean sucesso = true;
        try {
            sucesso = db.insert(tabela, null,
                    dados) > 0;
        }catch (Exception e){

            sucesso = false;
        }

        return sucesso;
    }

    public boolean deletar(String tabela, String campo, String id){

        boolean sucesso = true;

        sucesso = db.delete(tabela, campo+"=?",
                new String[]{id}) > 0;

        return  sucesso;
    }

    public boolean deletarDuasClaus(String tabela, String campo, String campo2, String cont1, String cont2){

        boolean sucesso = true;

        sucesso = db.delete(tabela, campo+"=? AND "+campo2+"=?",
                new String[]{cont1,cont2}) > 0;

        return  sucesso;
    }

    public boolean alterar(String tabela, String campo, String id, ContentValues dados){

        boolean sucesso = true;

        //int id = dados.getAsInteger(ident);

        sucesso = db.update(tabela, dados, campo+"=?",
                new String[]{id}) > 0;

        return  sucesso;
    }



    public ArrayList<Pedidos> getAllPedidos(){
        //Retorna todos os pedidos

        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getPedido_tabela()  + " ORDER BY " + RestauranteDataModel.getPedido_id() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco())));
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getPedidoId(int id){
        //Retorna todos os pedidos

        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getPedido_tabela()  + " WHERE " + RestauranteDataModel.getPedido_id() +
                " = '" + String.valueOf(id) + "' ORDER BY " + RestauranteDataModel.getPedido_id() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                //obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                //obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                //obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco())));
                //obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                //obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                //obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getMesasPedido(){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantas mesas estão abertas na tabela de pedidos
        //Além de agrupar sempre a mesa pelo primeiro id do pedido também retorna uma coluna a mais (preco_tot) com o valor total do pedido da mesa!
        //SELECT * , MIN(id_pedido), SUM(preco) AS 'preco_total' FROM tbl_pedido GROUP BY mesa ORDER BY mesa ASC
        String sql = "SELECT * , MIN (" + RestauranteDataModel.getPedido_id() + "), SUM (" + RestauranteDataModel.getPedido_preco() +
                ") AS '" + RestauranteDataModel.getPedido_preco_tot() +"' FROM " + RestauranteDataModel.getPedido_tabela() +
                " GROUP BY " + RestauranteDataModel.getPedido_mesa() + " ORDER BY " + RestauranteDataModel.getPedido_mesa() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                //obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                //obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                //obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                //obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                //obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                //obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                //obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getMesasPedido(int i){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantas mesas estão abertas na tabela de pedidos
        //Além de agrupar sempre a mesa pelo primeiro id do pedido também retorna uma coluna a mais (preco_tot) com o valor total do pedido da mesa!
        //SELECT * , MIN(id_pedido), SUM(preco) AS 'preco_total' FROM tbl_pedido GROUP BY mesa ORDER BY mesa ASC
        String sql = "SELECT * , MIN (" + RestauranteDataModel.getPedido_id() + "), SUM (" + RestauranteDataModel.getPedido_preco() +
                ") AS '" + RestauranteDataModel.getPedido_preco_tot() +"' FROM " + RestauranteDataModel.getPedido_tabela() +
                " GROUP BY " + RestauranteDataModel.getPedido_mesa() + " ORDER BY " + RestauranteDataModel.getPedido_mesa() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                //obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                //obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                //obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                //obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                //obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                //obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                //obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getMesasPedido(int num, String statusCozinha){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantas mesas estão abertas na tabela de pedidos
        //Além de agrupar sempre a mesa pelo primeiro id do pedido também retorna uma coluna a mais (preco_tot) com o valor total do pedido da mesa!
        //SELECT * , MIN(id_pedido), SUM(preco) AS 'preco_total' FROM tbl_pedido GROUP BY mesa ORDER BY mesa ASC LIMIT 6
        //SELECT * , MIN(id_pedido) FROM tbl_pedido WHERE status_cozinha = 'cozinha' and prato != '' GROUP BY mesa, status_cozinha ORDER BY mesa ASC LIMIT 6
        String sql = "SELECT * , MIN (" + RestauranteDataModel.getPedido_id() + ") FROM " + RestauranteDataModel.getPedido_tabela() +
                " WHERE " + RestauranteDataModel.getPedido_status() + " = '" + statusCozinha + "' AND " + RestauranteDataModel.getPedido_prato() +
                " != '' GROUP BY " + RestauranteDataModel.getPedido_mesa() + ", " + RestauranteDataModel.getPedido_status() + " ORDER BY " +
                RestauranteDataModel.getPedido_data_hora() + " ASC ";// LIMIT "+ String.valueOf(num);

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                //obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                //obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getNomesPorMesaPedido(String mesa){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantos nomes tem dentro do pedido da "Mesa 05" (tem que passar a mesa como argumento)
        //Retorna todos os nomes existentes na mesa com o valor total do pedido pelo nome
        //SELECT * ,SUM(preco) AS 'preco_total' FROM tbl_pedido WHERE mesa='Mesa 05' GROUP BY nome ORDER BY nome ASC
        String sql = "SELECT * , SUM (" + RestauranteDataModel.getPedido_preco() + ") AS '" + RestauranteDataModel.getPedido_preco_tot() +
                "' FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " + RestauranteDataModel.getPedido_mesa() + " = '" + mesa +
                "' GROUP BY " + RestauranteDataModel.getPedido_nome() + " ORDER BY " + RestauranteDataModel.getPedido_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getNomesPorMesaPedidoAll(String mesa, String statusCozinha){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantos nomes tem dentro do pedido da "Mesa 05" (tem que passar a mesa como argumento)
        //Retorna todos os nomes existentes na mesa com o valor total do pedido pelo nome
        //SELECT * , MIN(id_pedido) , SUM(prato_quant) AS 'prato_quant_tot' FROM tbl_pedido WHERE mesa='Mesa 03' AND status_cozinha='cozinha' GROUP BY prato ORDER BY id_pedido ASC
        String sql = "SELECT * , MIN (" + RestauranteDataModel.getPedido_id() + "), SUM (" + RestauranteDataModel.getPedido_quant_prato() + ") AS '" +
                RestauranteDataModel.getPedido_quant_prato_tot() + "' FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " +
                RestauranteDataModel.getPedido_mesa() + " = '" + mesa + "' AND " + RestauranteDataModel.getPedido_status() + " ='"
                + statusCozinha + "' AND " + RestauranteDataModel.getPedido_prato() + " != '' GROUP BY " + RestauranteDataModel.getPedido_prato() +
                " ORDER BY " + RestauranteDataModel.getPedido_id() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                //obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato_tot())));///////essa linha está pegando a quantidade total de pratos e não a quantidade que foi salva
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                //obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                //obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                //obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getNomesPorMesaPedidoAll(String mesa){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantos nomes tem dentro do pedido da "Mesa 05" (tem que passar a mesa como argumento)
        //Retorna todos os nomes existentes na mesa com o valor total do pedido pelo nome
        //SELECT * FROM tbl_pedido WHERE mesa='Mesa 03' ORDER BY id_pedido ASC
        String sql = " SELECT * FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " +
                RestauranteDataModel.getPedido_mesa() + " = '" + mesa + "' ORDER BY " + RestauranteDataModel.getPedido_id() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                //obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                //obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getNomesPorMesaPedidoAll(String mesa, String statusCozinha, int depois){
        //depois = 0 -> retorna apenas com o filtro da mesa e status da cozinha, ignorando a coluna de condicao_prato
        //depois = 1 -> retorna além dos filtros acima, o filtro de (condicao_prato = 1 || condicao_prato = 3) -> apenas depois
        //depois = 2 -> retorna além dos filtros acima, o filtro de (condicao_prato != 1 && condicao_prato != 3) -> apenas os que não são para depois

        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar em quantidade de linhas, quantos nomes tem dentro do pedido da "Mesa 05" (tem que passar a mesa como argumento)
        //Retorna todos os nomes existentes na mesa com o valor total do pedido pelo nome
        //SELECT * FROM tbl_pedido WHERE mesa='Mesa 03' AND status_cozinha='cozinha' ORDER BY id_pedido ASC
        //SELECT * FROM tbl_pedido WHERE mesa='Mesa 03' AND status_cozinha='cozinha' AND (condicao_prato = 1 OR condicao_prato = 3) ORDER BY id_pedido ASC
        //SELECT * FROM tbl_pedido WHERE mesa='Mesa 03' AND status_cozinha='cozinha' AND (condicao_prato != 1 AND condicao_prato != 3) ORDER BY id_pedido ASC

        String sql="";
        switch (depois) {
            case 0:
            sql = "SELECT * FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " + RestauranteDataModel.getPedido_mesa() +
                    " = '" + mesa + "' AND " + RestauranteDataModel.getPedido_status() + " ='" + statusCozinha + "' ORDER BY " +
                    RestauranteDataModel.getPedido_id() + " ASC ";
            break;
            case 1:
                sql = "SELECT * FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " + RestauranteDataModel.getPedido_mesa() +
                        " = '" + mesa + "' AND " + RestauranteDataModel.getPedido_status() + " ='" + statusCozinha + "' AND (" +
                        RestauranteDataModel.getPedido_condicao_prato() + " = 1 OR " + RestauranteDataModel.getPedido_condicao_prato() +
                        " = 3 " + " ) ORDER BY " + RestauranteDataModel.getPedido_id() + " ASC ";
                break;
            case 2:
                sql = "SELECT * FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " + RestauranteDataModel.getPedido_mesa() +
                        " = '" + mesa + "' AND " + RestauranteDataModel.getPedido_status() + " ='" + statusCozinha + "' AND (" +
                        RestauranteDataModel.getPedido_condicao_prato() + " != 1 AND " + RestauranteDataModel.getPedido_condicao_prato() +
                        " != 3 " + " ) ORDER BY " + RestauranteDataModel.getPedido_id() + " ASC ";
                break;
        }

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                //obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                //obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                //obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                //obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                //obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco_tot())));////essa linha está pegando o preço total e não o preço que foi salvo
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pedidos> getPedidoPorNomeMesa(String mesa, String nome, String order){
        //Retorna todos os pedidos
        Pedidos obj;

        // TIPADA
        ArrayList<Pedidos> lista = new ArrayList<>();

        //Formato da consulta que será realizada
        //Comando serve para retornar tod0 o pedido da pessoa da mesa por cliente (tem que passar a mesa e o nome como argumento)
        //SELECT * FROM tbl_pedido WHERE mesa="Mesa 05" AND nome LIKE "%Felipe%" ORDER BY prato ASC
        String sql = "SELECT * FROM " + RestauranteDataModel.getPedido_tabela() + " WHERE " + RestauranteDataModel.getPedido_mesa() + " = '" + mesa +
                "' AND " + RestauranteDataModel.getPedido_nome() + " LIKE '%" + nome + "%' ORDER BY " + order + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pedidos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_id())));
                obj.setIdPK(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_idPK())));
                obj.setMesa(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_mesa())));
                obj.setQuant_pessoas(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_pessoas())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome())));
                obj.setNome_usuario(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_nome_usuario())));
                obj.setPrato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_prato())));
                obj.setQuant_prato(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_prato())));
                obj.setCondicao_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPedido_condicao_prato())));
                obj.setAdicional(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_adicional())));
                obj.setRetirar(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_retirar())));
                obj.setBebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_bebida())));
                obj.setQuant_bebida(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_quant_bebida())));
                obj.setPreco(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_preco())));
                obj.setObs(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_obs())));
                obj.setData_hora(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_data_hora())));
                obj.setStatus(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPedido_status())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }


    public ArrayList<Ingredientes> getAllIngredientes(){

        //Retorna todos os ingredientes

        Ingredientes obj;

        // TIPADA
        ArrayList<Ingredientes> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getIngrediente_tabela()  + " ORDER BY " + RestauranteDataModel.getIngrediente_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Ingredientes();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_nome())));
                obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_tipo())));
                obj.setUnidade(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_unidade())));
                obj.setReferencia(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_referencia())));
                obj.setMedida(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida())));
                obj.setQuant(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant())));
                obj.setQuant_min(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant_min())));
                obj.setControle(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_controle())));
                obj.setMedida_ad(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida_ad())));
                obj.setCusto_uni(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_un())));
                obj.setCusto_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_ing())));
                obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ing())));
                obj.setPreco_ad(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ad())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Ingredientes> getAllIngredientesTipos(String tipo){

        //Retorna todos os ingredientes filtrados por tipo (doce/salgado)

        Ingredientes obj;

        // TIPADA
        ArrayList<Ingredientes> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getIngrediente_tabela() + " WHERE " +
                RestauranteDataModel.getIngrediente_tipo() + " LIKE '%" + tipo + "%' AND " +
                RestauranteDataModel.getIngrediente_controle() + " < 3 " +" ORDER BY " +
                RestauranteDataModel.getIngrediente_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Ingredientes();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_nome())));
                //obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_tipo())));
                //obj.setUnidade(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_unidade())));
                //obj.setReferencia(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_referencia())));
                //obj.setMedida(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida())));
                obj.setQuant(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant())));
                //obj.setQuant_min(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant_min())));
                obj.setControle(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_controle())));
                obj.setMedida_ad(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida_ad())));
                //obj.setCusto_uni(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_un())));
                //obj.setCusto_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_ing())));
                //obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ing())));
                obj.setPreco_ad(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ad())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Ingredientes> getFiltroIngredientesTipos(String tipo, String filtrar){

        //Retornar os ingredientes filtrados pelo digitação do usuário e também de acordo com o tipo que foi selecionado na tela

        Ingredientes obj;

        // TIPADA
        ArrayList<Ingredientes> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getIngrediente_tabela() + " WHERE " +
                RestauranteDataModel.getIngrediente_nome() + " LIKE '%" + filtrar + "%' " + " AND " +
                RestauranteDataModel.getIngrediente_tipo() + " LIKE '%" + tipo + "%' AND " +
                RestauranteDataModel.getIngrediente_controle() + " < 3 " +
                " ORDER BY " + RestauranteDataModel.getIngrediente_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Ingredientes();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_nome())));
                //obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_tipo())));
                //obj.setUnidade(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_unidade())));
                //obj.setReferencia(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_referencia())));
                //obj.setMedida(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida())));
                obj.setQuant(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant())));
                //obj.setQuant_min(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant_min())));
                obj.setControle(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_controle())));
                obj.setMedida_ad(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida_ad())));
                //obj.setCusto_uni(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_un())));
                //obj.setCusto_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_ing())));
                //obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ing())));
                obj.setPreco_ad(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ad())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public List<Ingredientes> getFiltroIngredientesId(int id){

        //Retornar os ingredientes filtrados pelo digitação do usuário e também de acordo com o tipo que foi selecionado na tela

        Ingredientes obj;

        // TIPADA
        List<Ingredientes> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getIngrediente_tabela() + " WHERE " +
                RestauranteDataModel.getIngrediente_id() + " = " + id + " ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Ingredientes();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_nome())));
                //obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_tipo())));
                //obj.setUnidade(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_unidade())));
                //obj.setReferencia(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_referencia())));
                //obj.setMedida(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida())));
                obj.setQuant(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant())));
                //obj.setQuant_min(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant_min())));
                obj.setControle(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_controle())));
                obj.setMedida_ad(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida_ad())));
                //obj.setCusto_uni(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_un())));
                //obj.setCusto_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_ing())));
                //obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ing())));
                obj.setPreco_ad(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ad())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Ingredientes> getFiltroIngredientesNome(String nome, int type){

        //Retornar os ingredientes filtrados pelo digitação do usuário e também de acordo com o tipo que foi selecionado na tela

        Ingredientes obj;

        // TIPADA
        ArrayList<Ingredientes> lista = new ArrayList<>();

        String sql="";

        if (type==1){
            sql = "SELECT * FROM " + RestauranteDataModel.getIngrediente_tabela() + " WHERE " +
                    RestauranteDataModel.getIngrediente_nome() + " LIKE '%" + nome + "%' " + " ORDER BY "
                    + RestauranteDataModel.getIngrediente_id() + " ASC ";
        } else {
            sql = "SELECT * FROM " + RestauranteDataModel.getIngrediente_tabela() + " WHERE " +
                    RestauranteDataModel.getIngrediente_nome() + " = '" + nome + "' ";
        }

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Ingredientes();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_nome())));
                obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_tipo())));
                obj.setUnidade(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_unidade())));
                //obj.setReferencia(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_referencia())));
                //obj.setMedida(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida())));
                obj.setQuant(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant())));
                obj.setQuant_min(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_quant_min())));
                obj.setControle(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_controle())));
                obj.setMedida_ad(cursor.getDouble(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_medida_ad())));
                //obj.setCusto_uni(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_un())));
                //obj.setCusto_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_custo_ing())));
                //obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ing())));
                obj.setPreco_ad(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getIngrediente_preco_ad())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }


    public ArrayList<Bebidas> getAllBebidas(){

        //Retorna todas as bebidas cadastradas

        Bebidas obj;

        // TIPADA
        ArrayList<Bebidas> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getBebida_tabela()  + " ORDER BY " + RestauranteDataModel.getBebida_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Bebidas();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getBebida_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_nome())));
                obj.setPreco_custo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_venda())));
                obj.setQuant_atual(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_atual())));
                obj.setQuant_min(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_min())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Bebidas> getFiltroBebidas(String filtrar){

        //Retorna as bebidas que contém o que foi digitado pelo usuário

        Bebidas obj;

        // TIPADA
        ArrayList<Bebidas> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getBebida_tabela() + " WHERE " +
                RestauranteDataModel.getBebida_nome() + " LIKE '%" + filtrar + "%' " +  " ORDER BY " +
                RestauranteDataModel.getBebida_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Bebidas();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getBebida_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_nome())));
                //obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_venda())));
                obj.setQuant_atual(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_atual())));
                obj.setQuant_min(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_min())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Bebidas> getBebidasNome(String nome){

        //Retorna as bebidas que contém o que foi digitado pelo usuário

        Bebidas obj;

        // TIPADA
        ArrayList<Bebidas> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getBebida_tabela() + " WHERE " +
                RestauranteDataModel.getBebida_nome() + " = '" + nome + "' " +  " ORDER BY " +
                RestauranteDataModel.getBebida_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Bebidas();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getBebida_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_nome())));
                //obj.setPreco_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_venda())));
                obj.setQuant_atual(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_atual())));
                obj.setQuant_min(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_min())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Bebidas> getIdBebidas(int id){

        //Retorna os pratos que contém o que usuário digitou

        Bebidas obj;

        // TIPADA
        ArrayList<Bebidas> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getBebida_tabela() + " WHERE " +
                RestauranteDataModel.getBebida_id() + " = " + id + " ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Bebidas();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getBebida_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_nome())));
                obj.setPreco_custo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_preco_venda())));
                obj.setQuant_atual(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_atual())));
                obj.setQuant_min(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getBebida_quant_min())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }


    public ArrayList<Pratos> getAllPratos(){

        //Retorna todos os pratos cadastrados

        Pratos obj;

        // TIPADA
        ArrayList<Pratos> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getPrato_tabela()  + " WHERE " + RestauranteDataModel.getPrato_subprato() +
                " = 1 " + " ORDER BY " + RestauranteDataModel.getPrato_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {

            do {

                obj = new Pratos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_nome())));
                obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_tipo())));
                obj.setSub_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_subprato())));
                obj.setIngredientes(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ingredientes())));
                obj.setQuant_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_quant_ing())));
                obj.setIng_quant(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ing_quant())));
                obj.setPreco_custo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_venda())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pratos> getFiltroPratos(String filtrar){

        //Retorna os pratos que contém o que usuário digitou

        Pratos obj;

        // TIPADA
        ArrayList<Pratos> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getPrato_tabela() + " WHERE " +
                RestauranteDataModel.getPrato_nome() + " LIKE '%" + filtrar + "%' " +  " AND " +
                RestauranteDataModel.getPrato_subprato() + " = 1 " + " ORDER BY " +
                RestauranteDataModel.getPrato_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pratos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_nome())));
                //obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_tipo())));
                obj.setSub_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_subprato())));
                obj.setIngredientes(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ingredientes())));
                obj.setQuant_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_quant_ing())));
                obj.setIng_quant(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ing_quant())));
                //obj.setPreco_custo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_venda())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pratos> getPratosNome(String nome, int subPrato){

        //Retorna os pratos que contém o que usuário digitou

        Pratos obj;

        // TIPADA
        ArrayList<Pratos> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getPrato_tabela() + " WHERE " +
                RestauranteDataModel.getPrato_nome() + " = '" + nome + "' " +  " AND " + RestauranteDataModel.getPrato_subprato() +
                " = " + subPrato + " ORDER BY " + RestauranteDataModel.getPrato_nome() + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pratos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_nome())));
                //obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_tipo())));
                obj.setSub_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_subprato())));
                obj.setIngredientes(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ingredientes())));
                obj.setQuant_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_quant_ing())));
                obj.setIng_quant(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ing_quant())));
                //obj.setPreco_custo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_venda())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Pratos> getIdPratos(int id){

        //Retorna os pratos que contém o que usuário digitou

        Pratos obj;

        // TIPADA
        ArrayList<Pratos> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getPrato_tabela() + " WHERE " +
                RestauranteDataModel.getPrato_id() + " = " + id + " AND " + RestauranteDataModel.getPrato_subprato() +
                " = 1 " + " ASC ";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Pratos();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_nome())));
                //obj.setTipo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_tipo())));
                obj.setSub_prato(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getPrato_subprato())));
                obj.setIngredientes(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ingredientes())));
                obj.setQuant_ing(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_quant_ing())));
                obj.setIng_quant(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_ing_quant())));
                //obj.setPreco_custo(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_custo())));
                obj.setPreco_venda(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getPrato_preco_venda())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }


    public ArrayList<Usuario> getNomeSenhaUsuarios(String nome, String senha){

        //Retorna os pratos que contém o que usuário digitou

        Usuario obj;

        // TIPADA
        ArrayList<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getUsuario_tabela() + " WHERE " +
                RestauranteDataModel.getUsuario_nome() + " = '" + nome + "' AND " + RestauranteDataModel.getUsuario_senha() +
                " = '" + senha + "' ORDER BY " + RestauranteDataModel.getUsuario_id() + " DESC";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Usuario();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getUsuario_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_nome())));
                obj.setEmail(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_email())));
                obj.setNivel(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_nivel())));
                obj.setSenha(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_senha())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    public ArrayList<Usuario> getNomeUsuarios(String nome){

        //Retorna os pratos que contém o que usuário digitou

        Usuario obj;

        // TIPADA
        ArrayList<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM " + RestauranteDataModel.getUsuario_tabela() + " WHERE " +
                RestauranteDataModel.getUsuario_nome() + " = '" + nome + "' ORDER BY " + RestauranteDataModel.getUsuario_id() + " DESC";

        cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {

            do {

                obj = new Usuario();

                obj.setId(cursor.getInt(cursor.getColumnIndex(RestauranteDataModel.getUsuario_id())));
                obj.setNome(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_nome())));
                obj.setEmail(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_email())));
                obj.setNivel(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_nivel())));
                obj.setSenha(cursor.getString(cursor.getColumnIndex(RestauranteDataModel.getUsuario_senha())));

                lista.add(obj);

            } while (cursor.moveToNext());

        }

        cursor.close();

        return lista;
    }

    /// aqui estão os métodos que serão relacionado ao WebService

    public void deletarTabela (String tabela){
        try {
            db.execSQL("DROP TABLE IF EXISTS " + tabela);
        } catch (Exception e) {
            Log.e("Banco de dados","Erro ao apagar tabela: "+e.getMessage());
        }
    }

    public void criarTabela (String queryCriarTabela){
        try {
            db.execSQL(queryCriarTabela);
        } catch (Exception e){
            Log.e("Banco de dados","Erro ao criar tabela: "+e.getMessage());
        }
    }

    public void backupBancoDeDados (){
        //Log.i("Escrita","Arquivo criado com sucesso!!1");

        File sd; //Caminho de destino do bd - Download
        File data; //Caminho de origem - data/data/pacote/db_name

        File arquivoBancoDeDados; //Nome do banco de dados
        File arquivoBackupBancoDeDados; //Nome do arquivo de backup

        FileChannel origem; // Leitura do arquivo original
        FileChannel destino; // Gravação do arquivo de destino com backup

        try {
            //Log.i("Escrita","Arquivo criado com sucesso!!2");

            sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            data = Environment.getDataDirectory();

            //Log.v("DB","SD - " + sd.getAbsolutePath());
            //Log.v("DB","DATA - " + data.getAbsolutePath());

            if (sd.canWrite()){
                //Log.i("Escrita","Arquivo criado com sucesso!!3");
                String nomeDoBancoDeDados =
                        "//data//com.codecorp.felipelima.bruxellas//databases/" + DB_NAME;

                String nomeDoArquivoDeBackup =
                        "bkp_" + DB_NAME;

                arquivoBancoDeDados = new File(data,nomeDoBancoDeDados);
                arquivoBackupBancoDeDados = new File(sd,nomeDoArquivoDeBackup);

                if (arquivoBancoDeDados.exists()){

                    origem = new FileInputStream(arquivoBancoDeDados).getChannel();
                    destino = new FileOutputStream(arquivoBackupBancoDeDados).getChannel();

                    destino.transferFrom(origem,0,origem.size());
                    origem.close();
                    destino.close();
                    //Log.i("Escrita", "Arquivo criado com sucesso!!4");
                    //if (arquivoBackupBancoDeDados.exists()) {
                    //Log.i("Escrita", "Arquivo criado com sucesso!!5");
                    //}
                }
            }


        } catch (Exception e){
            Log.e("DB","Erro: " + e.getMessage());
        }

    }

}
