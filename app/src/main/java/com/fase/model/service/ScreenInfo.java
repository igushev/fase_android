package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

public class ScreenInfo {

    @SerializedName("screen_id")
    private String screenId;

    public ScreenInfo() {
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }
}
