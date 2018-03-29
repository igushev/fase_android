package com.fase.model;

import java.util.List;

public class PrevStepButtonDataHolder {

    private List<String> idList;
    private String method;
    private Boolean requestLocale;

    public PrevStepButtonDataHolder(List<String> idList, String method, Boolean requestLocale) {
        this.idList = idList;
        this.method = method;
        this.requestLocale = requestLocale;
    }

    public List<String> getIdList() {
        return idList;
    }

    public String getMethod() {
        return method;
    }

    public Boolean getRequestLocale() {
        return requestLocale;
    }
}
