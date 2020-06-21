package com.codecorp.felipelima.bruxellas.model;

public class Ingredientes {

    private int id;
    private String nome;
    private String tipo;
    private String unidade;
    private String referencia;
    private int medida;
    private double quant;
    private double quant_min;
    private int controle;
    private double medida_ad;
    private String custo_uni;
    private String custo_ing;
    private String preco_ing;
    private String preco_ad;

    public Ingredientes () {

    }

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

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getMedida() {
        return medida;
    }

    public void setMedida(int medida) {
        this.medida = medida;
    }

    public double getQuant() {
        return quant;
    }

    public void setQuant(double quant) {
        this.quant = quant;
    }

    public double getQuant_min() {
        return quant_min;
    }

    public void setQuant_min(double quant_min) {
        this.quant_min = quant_min;
    }

    public int getControle() {
        return controle;
    }

    public void setControle(int controle) {
        this.controle = controle;
    }

    public double getMedida_ad() {
        return medida_ad;
    }

    public void setMedida_ad(double medida_ad) {
        this.medida_ad = medida_ad;
    }

    public String getCusto_uni() {
        return custo_uni;
    }

    public void setCusto_uni(String custo_uni) {
        this.custo_uni = custo_uni;
    }

    public String getCusto_ing() {
        return custo_ing;
    }

    public void setCusto_ing(String custo_ing) {
        this.custo_ing = custo_ing;
    }

    public String getPreco_ing() {
        return preco_ing;
    }

    public void setPreco_ing(String preco_ing) {
        this.preco_ing = preco_ing;
    }

    public String getPreco_ad() {
        return preco_ad;
    }

    public void setPreco_ad(String preco_ad) {
        this.preco_ad = preco_ad;
    }

}
