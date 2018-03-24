package com.fase.model;

import java.util.List;

public class UpdateValue {

    private String elementId;
    private List<String> idList;
    private String value;

    public UpdateValue() {
    }

    public UpdateValue(String elementId, List<String> idList, String value) {
        this.elementId = elementId;
        this.idList = idList;
        this.value = value;
    }

    public String getElementId() {
        return elementId;
    }

    public List<String> getIdList() {
        return idList;
    }

    public String getValue() {
        return value;
    }
}
