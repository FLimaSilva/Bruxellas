package com.codecorp.felipelima.bruxellas.datamodel;

public class RestauranteDataModel {

    // Dados para criar as tabelas no banco de dados
    // MOR - Modelo objeto relacional
    // TUPLA ou Registros

    //tabela dos pratos do restaurante
    private final static String prato_tabela = "tbl_pratos";
    private final static String prato_id = "id_prato";
    private final static String prato_nome = "nome_prato";
    private final static String prato_tipo = "tipo_prato";
    private final static String prato_subprato = "condicao_prato";
    private final static String prato_ingredientes = "ingredientes";
    private final static String prato_quant_ing = "quantidade_ingrediente";
    private final static String prato_ing_quant = "ingredientes_quantidade";
    private final static String prato_preco_custo = "preco_custo";
    private final static String prato_preco_venda = "preco_venda";

    //Tabela das bebidas do restaurante
    private final static String bebida_tabela = "tbl_bebidas";
    private final static String bebida_id = "id_bebida";
    private final static String bebida_nome = "nome_bebida";
    private final static String bebida_preco_custo = "preco_custo";
    private final static String bebida_preco_venda = "preco_venda";
    private final static String bebida_quant_atual = "quant_atual";
    private final static String bebida_quant_min = "quant_min";

    //Tabela dos ingredientes dos pratos do restaurante
    private final static String ingrediente_tabela = "tbl_ingredientes";
    private final static String ingrediente_id = "id_ingrediente";
    private final static String ingrediente_nome = "nome_ingrediente";
    private final static String ingrediente_tipo = "tipo_ingrediente";
    private final static String ingrediente_unidade = "unidade_medida";
    private final static String ingrediente_referencia = "referencia_medida";
    private final static String ingrediente_medida = "quantidade_medida";
    private final static String ingrediente_quant = "quantidade_atual";
    private final static String ingrediente_quant_min = "quantidade_minima";
    private final static String ingrediente_controle = "controle_produto";
    private final static String ingrediente_medida_ad = "quantidade_adicional";
    private final static String ingrediente_custo_un = "custo_unidade";
    private final static String ingrediente_custo_ing = "custo_ingrediente";
    private final static String ingrediente_preco_ing = "venda_ingrediente";
    private final static String ingrediente_preco_ad = "venda_adicional";

    //Tabela com os pedidos do restaurante
    private final static String pedido_tabela = "tbl_pedido";
    private final static String pedido_id = "id_pedido";
    private final static String pedido_idPK = "idpk_pedido";
    private final static String pedido_mesa = "mesa";
    private final static String pedido_quant_pessoas = "quant_pessoas"; // item novo
    private final static String pedido_nome = "nome"; //este é o nome do cliente que tá fazendo parte do pedido
    private final static String pedido_nome_usuario = "nome_usuario"; //este é o nome do usuário do app que tá fazendo o pedido
    private final static String pedido_prato = "prato";//"prato_nome";
    private final static String pedido_quant_prato = "prato_quant"; // item novo
    private final static String pedido_condicao_prato = "condicao_prato"; // item novo
    private final static String pedido_adicional = "adicional";
    private final static String pedido_retirar = "retirar";
    private final static String pedido_bebida = "bebida";//"bebida_nome";
    private final static String pedido_quant_bebida = "bebida_quant"; // item novo
    private final static String pedido_preco = "preco";
    private final static String pedido_obs = "obs";
    private final static String pedido_data_hora = "data_hora";
    private final static String pedido_status = "status_cozinha";
    private final static String pedido_preco_tot = "preco_tot"; // item novo, apenas serve para que eu possa fazer somas de maneira mais fácil
    private final static String pedido_quant_prato_tot = "prato_quant_tot"; // item novo, apenas serve para que eu possa fazer somas de maneira mais fácil

    //private final static String usuario_tabela = "tbl_usuario_app";
    //private final static String usuario_id = "id_usuario";
    //private final static String usuario_nome = "nome_usuario";
    //private final static String usuario_nivel = "nivel_usuario";
    //private final static String usuario_senha = "senha_usuario";
    private final static String usuario_tabela = "tbl_usuarios";
    private final static String usuario_id = "id_pessoa";
    private final static String usuario_nome = "usuario";
    private final static String usuario_email = "email";
    private final static String usuario_nivel = "nivel_acesso";//"nivel_acessoApp"; modificado no dia 01 de junho na visita no bruxellas
    private final static String usuario_senha = "senha";

    private static String queryCriarTabela = "";

    // Criar dinamicamente uma query SQL para criar
    // a tabela Média Escolar no Banco de Dados

    public static String criarTabelaPrato(){

        queryCriarTabela = "CREATE TABLE " + prato_tabela;
        queryCriarTabela += " ( ";
        queryCriarTabela += prato_id + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        queryCriarTabela += prato_nome + " TEXT, ";
        queryCriarTabela += prato_tipo + " TEXT, ";
        queryCriarTabela += prato_subprato + " INTEGER, ";
        queryCriarTabela += prato_ingredientes + " TEXT, ";
        queryCriarTabela += prato_quant_ing + " TEXT, ";
        queryCriarTabela += prato_ing_quant + " TEXT, ";
        queryCriarTabela += prato_preco_custo + " TEXT, ";
        queryCriarTabela += prato_preco_venda + " TEXT ";
        queryCriarTabela += " )";

        return  queryCriarTabela;
    }

    public static String criarTabelaBebida(){

        queryCriarTabela = "CREATE TABLE " + bebida_tabela;
        queryCriarTabela += " ( ";
        queryCriarTabela += bebida_id + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        queryCriarTabela += bebida_nome + " TEXT, ";
        queryCriarTabela += bebida_preco_custo + " TEXT, ";
        queryCriarTabela += bebida_preco_venda + " TEXT, ";
        queryCriarTabela += bebida_quant_atual + " TEXT, ";
        queryCriarTabela += bebida_quant_min + " TEXT ";
        queryCriarTabela += " ) ";

        return  queryCriarTabela;
    }

    public static String criarTabelaIngrediente(){

        queryCriarTabela = "CREATE TABLE " + ingrediente_tabela;
        queryCriarTabela += " ( ";
        queryCriarTabela += ingrediente_id + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        queryCriarTabela += ingrediente_nome + " TEXT, ";
        queryCriarTabela += ingrediente_tipo + " TEXT, ";
        queryCriarTabela += ingrediente_unidade + " TEXT, ";
        queryCriarTabela += ingrediente_referencia + " TEXT, ";
        queryCriarTabela += ingrediente_medida + " INTEGER, ";
        queryCriarTabela += ingrediente_quant + " DOUBLE, ";
        queryCriarTabela += ingrediente_quant_min + " DOUBLE, ";
        queryCriarTabela += ingrediente_controle + " INTEGER, ";
        queryCriarTabela += ingrediente_medida_ad + " DOUBLE, ";
        queryCriarTabela += ingrediente_custo_un + " TEXT, ";
        queryCriarTabela += ingrediente_custo_ing + " TEXT, ";
        queryCriarTabela += ingrediente_preco_ing + " TEXT, ";
        queryCriarTabela += ingrediente_preco_ad + " TEXT ";
        queryCriarTabela += ")";

        return  queryCriarTabela;
    }

    public static String criarTabelaPedido(){

        queryCriarTabela = "CREATE TABLE " + pedido_tabela;
        queryCriarTabela += " ( ";
        queryCriarTabela += pedido_id + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        queryCriarTabela += pedido_idPK + " INTEGER, ";
        queryCriarTabela += pedido_mesa + " INTEGER, ";
        queryCriarTabela += pedido_quant_pessoas + " TEXT, ";
        queryCriarTabela += pedido_nome + " TEXT, ";
        queryCriarTabela += pedido_nome_usuario + " TEXT, ";
        queryCriarTabela += pedido_prato + " TEXT, ";
        queryCriarTabela += pedido_quant_prato + " TEXT, ";
        queryCriarTabela += pedido_condicao_prato + " INTEGER, ";
        queryCriarTabela += pedido_adicional + " TEXT, ";
        queryCriarTabela += pedido_retirar + " TEXT, ";
        queryCriarTabela += pedido_bebida + " TEXT, ";
        queryCriarTabela += pedido_quant_bebida + " TEXT, ";
        queryCriarTabela += pedido_preco + " TEXT, ";
        queryCriarTabela += pedido_obs + " TEXT, ";
        queryCriarTabela += pedido_data_hora + " TEXT, ";
        queryCriarTabela += pedido_status + " TEXT ";
        queryCriarTabela += ")";

        return  queryCriarTabela;
    }

    public static String criarTabelaUsuario(){

        queryCriarTabela = "CREATE TABLE " + usuario_tabela;
        queryCriarTabela += " ( ";
        queryCriarTabela += usuario_id + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        queryCriarTabela += usuario_nome + " TEXT, ";
        queryCriarTabela += usuario_email + " TEXT, ";
        queryCriarTabela += usuario_nivel + " TEXT, ";
        queryCriarTabela += usuario_senha + " TEXT ";
        queryCriarTabela += " ) ";

        return  queryCriarTabela;
    }

    public static String getPrato_tabela() {
        return prato_tabela;
    }

    public static String getPrato_id() {
        return prato_id;
    }

    public static String getPrato_nome() {
        return prato_nome;
    }

    public static String getPrato_tipo() {
        return prato_tipo;
    }

    public static String getPrato_subprato() {
        return prato_subprato;
    }

    public static String getPrato_ingredientes() {
        return prato_ingredientes;
    }

    public static String getPrato_quant_ing() {
        return prato_quant_ing;
    }

    public static String getPrato_ing_quant() {
        return prato_ing_quant;
    }

    public static String getPrato_preco_custo() {
        return prato_preco_custo;
    }

    public static String getPrato_preco_venda() {
        return prato_preco_venda;
    }

    public static String getBebida_tabela() {
        return bebida_tabela;
    }

    public static String getBebida_id() {
        return bebida_id;
    }

    public static String getBebida_nome() {
        return bebida_nome;
    }

    public static String getBebida_preco_custo() {
        return bebida_preco_custo;
    }

    public static String getBebida_preco_venda() {
        return bebida_preco_venda;
    }

    public static String getBebida_quant_atual() {
        return bebida_quant_atual;
    }

    public static String getBebida_quant_min() {
        return bebida_quant_min;
    }

    public static String getIngrediente_tabela() {
        return ingrediente_tabela;
    }

    public static String getIngrediente_id() {
        return ingrediente_id;
    }

    public static String getIngrediente_nome() {
        return ingrediente_nome;
    }

    public static String getIngrediente_tipo() {
        return ingrediente_tipo;
    }

    public static String getIngrediente_unidade() {
        return ingrediente_unidade;
    }

    public static String getIngrediente_referencia() {
        return ingrediente_referencia;
    }

    public static String getIngrediente_medida() {
        return ingrediente_medida;
    }

    public static String getIngrediente_quant() {
        return ingrediente_quant;
    }

    public static String getIngrediente_quant_min() {
        return ingrediente_quant_min;
    }

    public static String getIngrediente_controle() {
        return ingrediente_controle;
    }

    public static String getIngrediente_medida_ad() {
        return ingrediente_medida_ad;
    }

    public static String getIngrediente_custo_un() {
        return ingrediente_custo_un;
    }

    public static String getIngrediente_custo_ing() {
        return ingrediente_custo_ing;
    }

    public static String getIngrediente_preco_ing() {
        return ingrediente_preco_ing;
    }

    public static String getIngrediente_preco_ad() {
        return ingrediente_preco_ad;
    }

    public static String getPedido_tabela() {
        return pedido_tabela;
    }

    public static String getPedido_id() {
        return pedido_id;
    }

    public static String getPedido_idPK() {
        return pedido_idPK;
    }

    public static String getPedido_mesa() {
        return pedido_mesa;
    }

    public static String getPedido_quant_pessoas() {
        return pedido_quant_pessoas;
    }

    public static String getPedido_nome() {
        return pedido_nome;
    }

    public static String getPedido_nome_usuario() {
        return pedido_nome_usuario;
    }

    public static String getPedido_prato() {
        return pedido_prato;
    }

    public static String getPedido_quant_prato() {
        return pedido_quant_prato;
    }

    public static String getPedido_condicao_prato() {
        return pedido_condicao_prato;
    }

    public static String getPedido_adicional() {
        return pedido_adicional;
    }

    public static String getPedido_retirar() {
        return pedido_retirar;
    }

    public static String getPedido_bebida() {
        return pedido_bebida;
    }

    public static String getPedido_quant_bebida() {
        return pedido_quant_bebida;
    }

    public static String getPedido_obs() {
        return pedido_obs;
    }

    public static String getPedido_data_hora() {
        return pedido_data_hora;
    }

    public static String getPedido_status() {
        return pedido_status;
    }

    public static String getPedido_preco() {
        return pedido_preco;
    }

    public static String getPedido_preco_tot() {
        return pedido_preco_tot;
    }

    public static String getPedido_quant_prato_tot() {
        return pedido_quant_prato_tot;
    }

    public static String getUsuario_tabela() {
        return usuario_tabela;
    }

    public static String getUsuario_id() {
        return usuario_id;
    }

    public static String getUsuario_nome() {
        return usuario_nome;
    }

    public static String getUsuario_email() {
        return usuario_email;
    }

    public static String getUsuario_nivel() {
        return usuario_nivel;
    }

    public static String getUsuario_senha() {
        return usuario_senha;
    }

}
