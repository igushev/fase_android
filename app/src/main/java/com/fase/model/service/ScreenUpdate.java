package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

public class ScreenUpdate {

    @SerializedName("elements_update")
    private ElementsUpdate elementsUpdate;
    private Device device;

    public ScreenUpdate() {
    }

    public ElementsUpdate getElementsUpdate() {
        return elementsUpdate;
    }

    public void setElementsUpdate(ElementsUpdate elementsUpdate) {
        this.elementsUpdate = elementsUpdate;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
