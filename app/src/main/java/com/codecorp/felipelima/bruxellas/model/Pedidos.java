package com.codecorp.felipelima.bruxellas.model;

public class Pedidos {

    private int id;//SQLite
    private int idPK;//Esse atributo é o id relacionado ao banco de dados do WebService (precisa desse cara pra alterar e deletar) - MySQL
    private int mesa;
    private String quant_pessoas;
    private String nome;
    private String nome_usuario;
    private String prato;
    private String quant_prato;
    private int condicao_prato;
    private String adicional;
    private String retirar;
    private String bebida;
    private String quant_bebida;
    private String preco;
    private String obs;
    private String data_hora;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPK() {
        return idPK;
    }

    public void setIdPK(int idPK) {
        this.idPK = idPK;
    }

    public int getMesa() {
        return mesa;
    }

    public void setMesa(int mesa) {
        this.mesa = mesa;
    }

    public String getQuant_pessoas() {
        return quant_pessoas;
    }

    public void setQuant_pessoas(String quant_pessoas) {
        this.quant_pessoas = quant_pessoas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome_usuario() {
        return nome_usuario;
    }

    public void setNome_usuario(String nome_usuario) {
        this.nome_usuario = nome_usuario;
    }

    public String getPrato() {
        return prato;
    }

    public void setPrato(String prato) {
        this.prato = prato;
    }

    public String getQuant_prato() {
        return quant_prato;
    }

    public void setQuant_prato(String quant_prato) {
        this.quant_prato = quant_prato;
    }

    public int getCondicao_prato() {
        return condicao_prato;
    }

    public void setCondicao_prato(int condicao_prato) {
        this.condicao_prato = condicao_prato;
    }

    public String getAdicional() {
        return adicional;
    }

    public void setAdicional(String adicional) {
        this.adicional = adicional;
    }

    public String getRetirar() {
        return retirar;
    }

    public void setRetirar(String retirar) {
        this.retirar = retirar;
    }

    public String getBebida() {
        return bebida;
    }

    public void setBebida(String bebida) {
        this.bebida = bebida;
    }

    public String getQuant_bebida() {
        return quant_bebida;
    }

    public void setQuant_bebida(String quant_bebida) {
        this.quant_bebida = quant_bebida;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getData_hora() {
        return data_hora;
    }

    public void setData_hora(String data_hora) {
        this.data_hora = data_hora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
