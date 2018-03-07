package com.fase.model.service;

public class Status extends BadRequest {

    private String message;

    public Status() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
