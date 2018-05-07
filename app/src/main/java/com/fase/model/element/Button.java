package com.fase.model.element;

import com.fase.model.enums.Align;
import com.google.gson.annotations.SerializedName;

public class Button extends VisualElement {

    @SerializedName("on_click")
    private Function onClick;
    private String text;
    private Align align;

    public Button() {
    }

    public Function getOnClick() {
        return onClick;
    }

    public void setOnClick(Function onClick) {
        this.onClick = onClick;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }
}
