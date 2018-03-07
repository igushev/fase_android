package com.fase.model.service;

import com.fase.model.data.Locale;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ElementCallback {

    @SerializedName("elements_update")
    private ElementsUpdate elementUpdate;
    @SerializedName("id_list")
    private List<String> idList;
    private String method;
    private Locale locale;
    private Device device;

    public ElementCallback() {
    }

    public ElementsUpdate getElementUpdate() {
        return elementUpdate;
    }

    public void setElementUpdate(ElementsUpdate elementUpdate) {
        this.elementUpdate = elementUpdate;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
