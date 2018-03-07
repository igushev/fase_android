package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElementsUpdate {

    @SerializedName("id_list_list")
    private List<List<String>> idListList;
    @SerializedName("value_list")
    private List<String> valueList;

    public ElementsUpdate() {
    }

    public List<List<String>> getIdListList() {
        return idListList;
    }

    public void setIdListList(List<List<String>> idListList) {
        this.idListList = idListList;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }
}
