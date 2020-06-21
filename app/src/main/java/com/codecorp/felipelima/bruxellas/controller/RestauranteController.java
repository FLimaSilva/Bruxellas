package com.codecorp.felipelima.bruxellas.controller;

import android.content.ContentValues;
import android.content.Context;

import com.codecorp.felipelima.bruxellas.datamodel.RestauranteDataModel;
import com.codecorp.felipelima.bruxellas.datasource.DataSource;
import com.codecorp.felipelima.bruxellas.model.Bebidas;
import com.codecorp.felipelima.bruxellas.model.Ingredientes;
import com.codecorp.felipelima.bruxellas.model.Pedidos;
import com.codecorp.felipelima.bruxellas.model.Pratos;
import com.codecorp.felipelima.bruxellas.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class RestauranteController extends DataSource{

    ContentValues dados;

    public RestauranteController(Context context) {
        super(context);
    }

    //Para o model de pedido é necessário salvar, alterar, deletar e listar os pedidos
    public boolean salvarPedido(Pedidos obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getPedido_idPK(), obj.getIdPK());
        dados.put(RestauranteDataModel.getPedido_mesa(), obj.getMesa());
        dados.put(RestauranteDataModel.getPedido_quant_pessoas(), obj.getQuant_pessoas());
        dados.put(RestauranteDataModel.getPedido_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getPedido_nome_usuario(), obj.getNome_usuario());
        dados.put(RestauranteDataModel.getPedido_prato(), obj.getPrato());
        dados.put(RestauranteDataModel.getPedido_quant_prato(), obj.getQuant_prato());
        dados.put(RestauranteDataModel.getPedido_condicao_prato(), obj.getCondicao_prato());
        dados.put(RestauranteDataModel.getPedido_adicional(), obj.getAdicional());
        dados.put(RestauranteDataModel.getPedido_retirar(), obj.getRetirar());
        dados.put(RestauranteDataModel.getPedido_bebida(), obj.getBebida());
        dados.put(RestauranteDataModel.getPedido_quant_bebida(), obj.getQuant_bebida());
        dados.put(RestauranteDataModel.getPedido_preco(), obj.getPreco());
        dados.put(RestauranteDataModel.getPedido_obs(), obj.getObs());
        dados.put(RestauranteDataModel.getPedido_data_hora(), obj.getData_hora());
        dados.put(RestauranteDataModel.getPedido_status(), obj.getStatus());

        sucesso = insert(RestauranteDataModel.getPedido_tabela(), dados);

        return sucesso;
    }

    public boolean deletarPedidoId(int id){

        boolean sucesso = true;

        sucesso = deletar(RestauranteDataModel.getPedido_tabela(),RestauranteDataModel.getPedido_id(),String.valueOf(id));

        return sucesso;
    }

    public boolean deletarPedidoMesa(String mesa){

        boolean sucesso = true;

        sucesso = deletar(RestauranteDataModel.getPedido_tabela(),RestauranteDataModel.getPedido_mesa(),mesa);

        return sucesso;
    }

    public boolean deletarPedidoNome(String mesa, String nome){

        boolean sucesso = true;

        sucesso = deletarDuasClaus(RestauranteDataModel.getPedido_tabela(),
                RestauranteDataModel.getPedido_mesa(),RestauranteDataModel.getPedido_nome(),
                mesa,nome);

        return sucesso;
    }

    public boolean alterarPedido(Pedidos obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getPedido_id(), obj.getId());
        dados.put(RestauranteDataModel.getPedido_mesa(), obj.getMesa());
        dados.put(RestauranteDataModel.getPedido_quant_pessoas(), obj.getQuant_pessoas());
        dados.put(RestauranteDataModel.getPedido_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getPedido_nome_usuario(), obj.getNome_usuario());
        dados.put(RestauranteDataModel.getPedido_prato(), obj.getPrato());
        dados.put(RestauranteDataModel.getPedido_quant_prato(), obj.getQuant_prato());
        dados.put(RestauranteDataModel.getPedido_condicao_prato(), obj.getCondicao_prato());
        dados.put(RestauranteDataModel.getPedido_adicional(), obj.getAdicional());
        dados.put(RestauranteDataModel.getPedido_retirar(), obj.getRetirar());
        dados.put(RestauranteDataModel.getPedido_bebida(), obj.getBebida());
        dados.put(RestauranteDataModel.getPedido_quant_bebida(), obj.getQuant_bebida());
        dados.put(RestauranteDataModel.getPedido_preco(), obj.getPreco());
        dados.put(RestauranteDataModel.getPedido_obs(), obj.getObs());
        //dados.put(RestauranteDataModel.getPedido_data_hora(), obj.getData_hora());
        dados.put(RestauranteDataModel.getPedido_status(), obj.getStatus());

        //(String tabela, String campo, String id, ContentValues dados)
        sucesso = alterar(RestauranteDataModel.getPedido_tabela(),RestauranteDataModel.getPedido_id(),
                String.valueOf(obj.getId()),dados);

        return sucesso;
    }

    public boolean alterarPedidoCoz(Pedidos obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getPedido_id(), obj.getId());
        //dados.put(RestauranteDataModel.getPedido_mesa(), obj.getMesa());
        //dados.put(RestauranteDataModel.getPedido_quant_pessoas(), obj.getQuant_pessoas());
        //dados.put(RestauranteDataModel.getPedido_nome(), obj.getNome());
        //dados.put(RestauranteDataModel.getPedido_quant_prato(), obj.getQuant_prato());
        //dados.put(RestauranteDataModel.getPedido_prato(), obj.getPrato());
        //dados.put(RestauranteDataModel.getPedido_condicao_prato(), obj.getCondicao_prato());
        //dados.put(RestauranteDataModel.getPedido_adicional(), obj.getAdicional());
        //dados.put(RestauranteDataModel.getPedido_retirar(), obj.getRetirar());
        //dados.put(RestauranteDataModel.getPedido_bebida(), obj.getBebida());
        //dados.put(RestauranteDataModel.getPedido_quant_bebida(), obj.getQuant_bebida());
        //dados.put(RestauranteDataModel.getPedido_preco(), obj.getPreco());
        //dados.put(RestauranteDataModel.getPedido_obs(), obj.getObs());
        //dados.put(RestauranteDataModel.getPedido_data_hora(), obj.getData_hora());
        dados.put(RestauranteDataModel.getPedido_status(), obj.getStatus());

        //(String tabela, String campo, String id, ContentValues dados)
        sucesso = alterar(RestauranteDataModel.getPedido_tabela(),RestauranteDataModel.getPedido_id(),
                String.valueOf(obj.getId()),dados);

        return sucesso;
    }


    public ArrayList<Pedidos> listarPedido (){
        return getAllPedidos();
    } //Neste list os pedidos são retornados organizados de acordo com o id do pedido (quem pediu primeiro vem primeiro)

    public ArrayList<Pedidos> listarPedidoId (int id){
        return getPedidoId(id);
    } //Neste list os pedidos são retornados organizados de acordo com o id do pedido (quem pediu primeiro vem primeiro)

    public ArrayList<Pedidos> listarMesasPedido (){
        return getMesasPedido();
    }

    public ArrayList<Pedidos> listarMesasPedidoLimite (int num, String statusCozinha){
        return getMesasPedido(num,statusCozinha);
    }

    public ArrayList<Pedidos> listarNomesPorMesaPedido(String mesa){
        return getNomesPorMesaPedido(mesa);
    }

    public ArrayList<Pedidos> listarNomesPorMesaPedidoAll(String mesa){
        return getNomesPorMesaPedidoAll(mesa);
    }

    public ArrayList<Pedidos> listarNomesPorMesaPedidoAll(String mesa, String statusCozinha){
        return getNomesPorMesaPedidoAll(mesa,statusCozinha);
    }

    public ArrayList<Pedidos> listarNomesPorMesaPedidoAllNoGroup(String mesa, String statusCozinha,int depois){
        return getNomesPorMesaPedidoAll(mesa,statusCozinha, depois);
    }

    public ArrayList<Pedidos> listarPedidoPorNomeMesa (String mesa, String nome){
        return getPedidoPorNomeMesa(mesa,nome,RestauranteDataModel.getPedido_prato());
    }

    public ArrayList<Pedidos> listarPedidoPorNomeMesaOrdId (String mesa, String nome){
        return getPedidoPorNomeMesa(mesa,nome,RestauranteDataModel.getPedido_id());
    }


    //Para o model de ingredientes é necessário apenas listar, pois os ingredientes serão demonstrados
    // na tela onde será escolhido o adicional
    public boolean salvarIngredientes(Ingredientes obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getIngrediente_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getIngrediente_tipo(), obj.getTipo());
        dados.put(RestauranteDataModel.getIngrediente_unidade(), obj.getUnidade());
        dados.put(RestauranteDataModel.getIngrediente_referencia(), obj.getReferencia());
        dados.put(RestauranteDataModel.getIngrediente_medida(), obj.getMedida());
        dados.put(RestauranteDataModel.getIngrediente_quant(), obj.getQuant());
        dados.put(RestauranteDataModel.getIngrediente_quant_min(), obj.getQuant_min());
        dados.put(RestauranteDataModel.getIngrediente_controle(), obj.getControle());
        dados.put(RestauranteDataModel.getIngrediente_medida_ad(), obj.getMedida_ad());
        dados.put(RestauranteDataModel.getIngrediente_custo_un(), obj.getCusto_uni());
        dados.put(RestauranteDataModel.getIngrediente_custo_ing(), obj.getCusto_ing());
        //dados.put(RestauranteDataModel.getIngrediente_preco_ing(), obj.getPreco_ing());
        dados.put(RestauranteDataModel.getIngrediente_preco_ad(), obj.getPreco_ad());

        sucesso = insert(RestauranteDataModel.getIngrediente_tabela(), dados);

        return sucesso;
    }

    public boolean deletarIngredientes(Ingredientes obj){

        boolean sucesso = true;

        sucesso = deletar(RestauranteDataModel.getIngrediente_tabela(),RestauranteDataModel.getIngrediente_id(),String.valueOf(obj.getId()));

        return sucesso;
    }

    /*public boolean alterarIngredientes(Ingredientes obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getIngrediente_id(), obj.getId());
        dados.put(RestauranteDataModel.getIngrediente_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getIngrediente_tipo(), obj.getTipo());
        dados.put(RestauranteDataModel.getIngrediente_quant(), obj.getQuant());
        dados.put(RestauranteDataModel.getIngrediente_quant_min(), obj.getQuant_min());
        dados.put(RestauranteDataModel.getIngrediente_preco_custo(), obj.getPreco_ing());
        dados.put(RestauranteDataModel.getIngrediente_preco_venda(), obj.getPreco_ad());

        sucesso = alterar(RestauranteDataModel.getIngrediente_tabela(), dados);

        return sucesso;
    }*/

    public ArrayList<Ingredientes> listarIngredientes (){// consulta será utilizada para listar os ingredientes e quantidades respectivas no estoque
        return getAllIngredientes();
    } //Neste list os ingredientes são retornados organizados em ordem alfabética pelo seu nome

    public ArrayList<Ingredientes> listarIngredientes (String tipo){
        return getAllIngredientesTipos(tipo);
    } //Neste list os ingredientes são retornados de acordo com seu tipo e organizados em ordem alfabética pelo seu nome

    public ArrayList<Ingredientes> listarIngredientes (String tipo, String filtro){
        return getFiltroIngredientesTipos(tipo,filtro);
    } //Neste list os ingredientes são retornados de acordo com seu tipo, filtrados pelo que o usuário digitou
      // e organizados em ordem alfabética pelo seu nome

    public List<Ingredientes> listarIngredientes (int id){
        return getFiltroIngredientesId(id);
    } //Neste list os ingredientes são retornados de acordo com seu id

    public ArrayList<Ingredientes> listarIngredientesNome (String nome, int type){// se der problema esse cara aqui era só List e não ArrayList
        return getFiltroIngredientesNome(nome, type);
    } //Neste list os ingredientes são retornados de acordo com seu id


    //Para o model de bebidas é necessário apenas listar
    public boolean salvarBebidas(Bebidas obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getBebida_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getBebida_preco_custo(), obj.getPreco_custo());
        dados.put(RestauranteDataModel.getBebida_preco_venda(), obj.getPreco_venda());
        dados.put(RestauranteDataModel.getBebida_quant_atual(), obj.getQuant_atual());
        dados.put(RestauranteDataModel.getBebida_quant_min(), obj.getQuant_min());

        sucesso = insert(RestauranteDataModel.getBebida_tabela(), dados);

        return sucesso;
    }

    public boolean deletarBebidas(Bebidas obj){

        boolean sucesso = true;

        sucesso = deletar(RestauranteDataModel.getBebida_tabela(),RestauranteDataModel.getBebida_id(),String.valueOf(obj.getId()));

        return sucesso;
    }

    /*public boolean alterarBebidas(Bebidas obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getBebida_id(), obj.getId());
        dados.put(RestauranteDataModel.getBebida_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getBebida_preco_custo(), obj.getPreco_ing());
        dados.put(RestauranteDataModel.getBebida_preco_venda(), obj.getPreco_ad());

        sucesso = alterar(RestauranteDataModel.getBebida_tabela(), dados);

        return sucesso;
    }*/

    public ArrayList<Bebidas> listarBebidas (){
        return getAllBebidas();
    } //Neste list as bebidas são retornadas organizadas em ordem alfabética pelo seu nome

    public ArrayList<Bebidas> listarBebidas (String filtro){
        return getFiltroBebidas(filtro);
    } //Neste list as bebidas são retornadas de acordo com o que foi digitado pelo usuário e
    // organizadas em ordem alfabética pelo seu nome

    public ArrayList<Bebidas> listarBebidasNome (String nome){
        return getBebidasNome(nome);
    } //Neste list as bebidas são retornadas de acordo com o nome salvo no banco de dados
    // organizadas em ordem alfabética pelo seu nome

    public ArrayList<Bebidas> listarBebidas (int id){
        return getIdBebidas(id);
    } //Neste list o prato é retornado de acordo com seu id


    //Para o model de pratos é necessário apenas listar
    public boolean salvarPratos(Pratos obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getPrato_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getPrato_tipo(), obj.getTipo());
        dados.put(RestauranteDataModel.getPrato_subprato(), obj.getSub_prato());
        dados.put(RestauranteDataModel.getPrato_ingredientes(), obj.getIngredientes());
        dados.put(RestauranteDataModel.getPrato_quant_ing(), obj.getQuant_ing());
        dados.put(RestauranteDataModel.getPrato_ing_quant(), obj.getIng_quant());
        dados.put(RestauranteDataModel.getPrato_preco_custo(), obj.getPreco_custo());
        dados.put(RestauranteDataModel.getPrato_preco_venda(), obj.getPreco_venda());

        sucesso = insert(RestauranteDataModel.getPrato_tabela(), dados);

        return sucesso;
    }

    public boolean deletarPratos(Pratos obj){

        boolean sucesso = true;

        sucesso = deletar(RestauranteDataModel.getPrato_tabela(),RestauranteDataModel.getPrato_id(),String.valueOf(obj.getId()));

        return sucesso;
    }

    /*public boolean alterarPratos(Pratos obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getPrato_id(), obj.getId());
        dados.put(RestauranteDataModel.getPrato_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getPrato_ingrediente1(), obj.getIngrediente1());
        dados.put(RestauranteDataModel.getPrato_ingrediente2(), obj.getIngrediente2());
        dados.put(RestauranteDataModel.getPrato_ingrediente3(), obj.getIngrediente3());
        dados.put(RestauranteDataModel.getPrato_ingrediente4(), obj.getIngrediente4());
        dados.put(RestauranteDataModel.getPrato_ingrediente5(), obj.getIngrediente5());
        dados.put(RestauranteDataModel.getPrato_ingrediente6(), obj.getIngrediente6());
        dados.put(RestauranteDataModel.getPrato_ingrediente7(), obj.getIngrediente7());
        dados.put(RestauranteDataModel.getPrato_ingrediente8(), obj.getIngrediente8());
        dados.put(RestauranteDataModel.getPrato_ingrediente9(), obj.getIngrediente9());
        dados.put(RestauranteDataModel.getPrato_ingrediente10(), obj.getIngrediente10());
        dados.put(RestauranteDataModel.getPrato_preco_custo(), obj.getPreco_ing());
        dados.put(RestauranteDataModel.getPrato_preco_venda(), obj.getPreco_ad());

        sucesso = alterar(RestauranteDataModel.getPrato_tabela(), dados);

        return sucesso;
    }*/

    public ArrayList<Pratos> listarPratos (){
        return getAllPratos();
    } //Neste list os pratos são retornados organizados em ordem alfabética pelo seu nome

    public ArrayList<Pratos> listarPratos (String filtro){
        return getFiltroPratos(filtro);
    } //Neste list os pratos são retornados de acordo com o digitado pelo usuário e organizados em ordem alfabética pelo seu nome

    public ArrayList<Pratos> listarPratosNome (String nome, int subPrato){
        return getPratosNome(nome,subPrato);
    } //Neste list os pratos são retornados de acordo com o nome do prato que foi salvo no banco de dados em ordem alfabética pelo seu nome

    public ArrayList<Pratos> listarPratos (int id){
        return getIdPratos(id);
    } //Neste list o prato é retornado de acordo com seu id


    public boolean salvarUsuarios (Usuario obj){

        boolean sucesso = true;

        dados = new ContentValues();

        dados.put(RestauranteDataModel.getUsuario_nome(), obj.getNome());
        dados.put(RestauranteDataModel.getUsuario_email(), obj.getEmail());
        dados.put(RestauranteDataModel.getUsuario_nivel(), obj.getNivel());
        dados.put(RestauranteDataModel.getUsuario_senha(), obj.getSenha());

        sucesso = insert(RestauranteDataModel.getUsuario_tabela(), dados);

        return sucesso;
    }

    public  ArrayList<Usuario> listarUsuariosNomeSenha (String nome, String senha){
        return getNomeSenhaUsuarios(nome,senha);
    }

    public ArrayList<Usuario> listarUsuariosNome (String nome){
        return getNomeUsuarios(nome);
    }

}
