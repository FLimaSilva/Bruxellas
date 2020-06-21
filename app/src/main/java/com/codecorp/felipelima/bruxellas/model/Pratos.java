package com.codecorp.felipelima.bruxellas.model;

public class Pratos {

    private int id;
    private String nome;
    private String tipo;
    private int sub_prato;//Se for 1 é um prato comum, já se for 2 é um subprato
    private String quant;//esse campo é a quantidade que foi escolhida ao efetuar o pedido, ou seja,
                         // ela não vai vir em uma consulta do banco de dados nunca.
                         // Será apenas utilizado na estrutura de pratos pedidos (Activity - EscolherPrato e Adicionar Prato)
                         // Por fim nas consultas na tela de fechamento do pedido, e no ícone de consultar e salvar o pedido na tela inicial
    private String status;//esse campo é o status em que se encontra o prato na cozinha, ou seja,
                         // ela não vai vir em uma consulta do banco de dados nunca.
                         // Será apenas utilizado na estrutura da aba da cozinha. Por fim na tela de fechamento do pedido,
                         // apenas nos casos de alteração do pedido, pois no caso de um pedido novo, ele será nulo
    private int id_pedido;//esse campo é o id do pedido, esse campo será sempre nulo, exceto quando o cliente em questão for selecionado para
                         // alterar os pratos que devem ser pedidos
                         // apenas nos casos de alteração do pedido, pois no caso de um pedido novo, ele será nulo
    private int cond_prato;//esse campo é a condição em que o prato será passado para cozinha, ou seja,
                           // ele não vai vir em uma consulta do banco de dados nunca.
                           // Será utilizado na estrutura de pratos pedidos (Activity - EscolherPrato e Adicionar Prato)
                           // Será utilizado na estrutura da aba da cozinha. Por fim na tela de fechamento do pedido(para lançamento no banco)
    private String ingredientes;
    private String quant_ing;
    private String ing_quant;
    private String preco_custo;
    private String preco_venda;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getSub_prato() {
        return sub_prato;
    }

    public void setSub_prato(int sub_prato) {
        this.sub_prato = sub_prato;
    }

    public String getQuant() {
        return quant;
    }

    public void setQuant(String quant) {
        this.quant = quant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getCond_prato() {
        return cond_prato;
    }

    public void setCond_prato(int cond_prato) {
        this.cond_prato = cond_prato;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getQuant_ing() {
        return quant_ing;
    }

    public void setQuant_ing(String quant_ing) {
        this.quant_ing = quant_ing;
    }

    public String getIng_quant() {
        return ing_quant;
    }

    public void setIng_quant(String ing_quant) {
        this.ing_quant = ing_quant;
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
}
