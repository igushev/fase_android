package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

public class SessionInfo {

    @SerializedName("session_id")
    private String sessionId;

    public SessionInfo() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
