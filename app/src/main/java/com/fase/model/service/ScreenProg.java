package com.fase.model.service;

import com.fase.model.element.Screen;
import com.google.gson.annotations.SerializedName;

public class ScreenProg {

    private Screen screen;
    @SerializedName("recent_device")
    private Device recentDevice;
    @SerializedName("elements_update")
    private ElementsUpdate elementsUpdate;
    @SerializedName("session_id")
    private String sessionId;

    public ScreenProg() {
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Device getRecentDevice() {
        return recentDevice;
    }

    public void setRecentDevice(Device recentDevice) {
        this.recentDevice = recentDevice;
    }

    public ElementsUpdate getElementsUpdate() {
        return elementsUpdate;
    }

    public void setElementsUpdate(ElementsUpdate elementsUpdate) {
        this.elementsUpdate = elementsUpdate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
