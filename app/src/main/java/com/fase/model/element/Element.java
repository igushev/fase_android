package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

public class Element {

    @SerializedName("__class__")
    private String className;

    public Element() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
