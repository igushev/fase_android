package com.fase.model;

import android.widget.EditText;

import java.util.ArrayList;

public class RequestContactDataHolder {

    EditText contactTextView;
    private String elementId;
    private ArrayList<String> idList;
    private String method;
    private Boolean requestLocale;

    public RequestContactDataHolder(EditText contactTextView, String elementId, ArrayList<String> idList, String method, Boolean requestLocale) {
        this.contactTextView = contactTextView;
        this.elementId = elementId;
        this.idList = idList;
        this.method = method;
        this.requestLocale = requestLocale;
    }

    public EditText getContactTextView() {
        return contactTextView;
    }

    public String getElementId() {
        return elementId;
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
}
