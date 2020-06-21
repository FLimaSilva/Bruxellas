package com.codecorp.felipelima.bruxellas.model;

public class Bebidas {

    private int id;
    private String nome;
    private String quant;//esse campo é a quantidade que foi escolhida ao efetuar o pedido, ou seja,
                         // ela não vai vir em uma consulta do banco de dados nunca.
                         // Será apenas utilizado na estrutura de pratos pedidos (Activity - EscolherPrato e Adicionar Prato)
                         // Por fim nas consultas na tela de fechamento do pedido, e no ícone de consultar e salvar o pedido na tela inicial
    private int id_pedido;//esse campo é o id do pedido, esse campo será sempre nulo, exceto quando o cliente em questão for selecionado para
                          // alterar os pratos que devem ser pedidos
                          // apenas nos casos de alteração do pedido, pois no caso de um pedido novo, ele será nulo
    private String preco_custo;
    private String preco_venda;
    private String quant_atual;
    private String quant_min;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuant() {
        return quant;
    }

    public void setQuant(String quant) {
        this.quant = quant;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getPreco_custo() {
        return preco_custo;
    }

    public void setPreco_custo(String preco_custo) {
        this.preco_custo = preco_custo;
    }

    public String getPreco_venda() {
        return preco_venda;
    }

    public void setPreco_venda(String preco_venda) {
        this.preco_venda = preco_venda;
    }

    public String getQuant_atual() {
        return quant_atual;
    }

    public void setQuant_atual(String quant_atual) {
        this.quant_atual = quant_atual;
    }

    public String getQuant_min() {
        return quant_min;
    }

    public void setQuant_min(String quant_min) {
        this.quant_min = quant_min;
    }
}
