package com.fase.model.element;

import com.fase.model.enums.Align;

public class Switch extends VisualElement {

    private Align align;
    private String text;
    private Boolean value;

    public Switch() {
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
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
