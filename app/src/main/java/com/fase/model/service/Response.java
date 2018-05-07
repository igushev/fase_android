package com.fase.model.service;

import com.fase.model.data.VersionInfo;
import com.fase.model.element.Screen;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("elements_update")
    private ElementsUpdate elementsUpdate;
    @SerializedName("screen_info")
    private ScreenInfo screenInfo;
    @SerializedName("session_info")
    private SessionInfo sessionInfo;
    private Screen screen;
    private Resources resources;
    @SerializedName("version_info")
    private VersionInfo versionInfo;

    public Response() {
    }

    public ElementsUpdate getElementsUpdate() {
        return elementsUpdate;
    }

    public void setElementsUpdate(ElementsUpdate elementsUpdate) {
        this.elementsUpdate = elementsUpdate;
    }

    public ScreenInfo getScreenInfo() {
        return screenInfo;
    }

    public void setScreenInfo(ScreenInfo screenInfo) {
        this.screenInfo = screenInfo;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }
}
