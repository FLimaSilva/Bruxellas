package com.codecorp.felipelima.bruxellas.eventbus;

import com.codecorp.felipelima.bruxellas.model.Pedidos;

import java.util.List;

public class MessageEB {
    private List<Pedidos> list;
    private int number;
    private String text;
    private String classTester;

    public List<Pedidos> getList() {
        return list;
    }

    public void setList(List<Pedidos> list) {
        this.list = list;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassTester() {
        return classTester;
    }

    public void setClassTester(String classTester) {
        this.classTester = classTester;
    }
}
