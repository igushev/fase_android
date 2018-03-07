package com.fase.model.element;

import com.fase.model.enums.fase.Alight;
import com.fase.model.enums.fase.Size;
import com.google.gson.annotations.SerializedName;

public class Label extends VisualElement {

    @SerializedName("on_click")
    private Function onClick;
    private Alight alight;
    private Float font;
    private Size size;
    private String text;

    public Label() {
    }

    public Function getOnClick() {
        return onClick;
    }

    public void setOnClick(Function onClick) {
        this.onClick = onClick;
    }

    public Alight getAlight() {
        return alight;
    }

    public void setAlight(Alight alight) {
        this.alight = alight;
    }

    public Float getFont() {
        return font;
    }

    public void setFont(Float font) {
        this.font = font;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
