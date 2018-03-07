package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElementContainer extends Element {

    @SerializedName("id_element_list")
    protected List<Tuple> idElementList;

    public ElementContainer() {
    }

    public List<Tuple> getIdElementList() {
        return idElementList;
    }

    public void setIdElementList(List<Tuple> idElementList) {
        this.idElementList = idElementList;
    }
}
