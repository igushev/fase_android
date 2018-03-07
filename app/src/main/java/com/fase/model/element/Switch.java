package com.fase.model.element;

import com.fase.model.enums.fase.Alight;

public class Switch extends VisualElement {

    private Alight alight;
    private String text;
    private Boolean value;

    public Switch() {
    }

    public Alight getAlight() {
        return alight;
    }

    public void setAlight(Alight alight) {
        this.alight = alight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
