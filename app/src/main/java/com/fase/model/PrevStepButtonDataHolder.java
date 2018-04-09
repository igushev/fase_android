package com.fase.model;

import com.fase.model.element.Tuple;

import java.util.ArrayList;

public class PrevStepButtonDataHolder {

    private Tuple tuple;
    private ArrayList<String> idList;
    private String method;
    private Boolean requestLocale;

    public PrevStepButtonDataHolder(ArrayList<String> idList, String method, Boolean requestLocale) {
        this.idList = idList;
        this.method = method;
        this.requestLocale = requestLocale;
    }

    public PrevStepButtonDataHolder(Tuple tuple, ArrayList<String> idList, String method, Boolean requestLocale) {
        this.tuple = tuple;
        this.idList = idList;
        this.method = method;
        this.requestLocale = requestLocale;
    }

    public ArrayList<String> getIdList() {
        return idList;
    }

    public String getMethod() {
        return method;
    }

    public Boolean getRequestLocale() {
        return requestLocale;
    }

    public Tuple getTuple() {
        return tuple;
    }
}
