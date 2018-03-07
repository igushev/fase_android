package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

public class Button extends VisualElement {

    @SerializedName("on_click")
    private Function onClick;
    private String image;
    private String text;

    public Button() {
    }

    public Function getOnClick() {
        return onClick;
    }

    public void setOnClick(Function onClick) {
        this.onClick = onClick;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
