package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

public class Function {

    @SerializedName("__func__")
    private String funtion;
    @SerializedName("__module__")
    private String module;

    public Function() {
    }

    public String getFuntion() {
        return funtion;
    }

    public void setFuntion(String funtion) {
        this.funtion = funtion;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
