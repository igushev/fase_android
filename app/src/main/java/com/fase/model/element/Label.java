package com.fase.model.element;

import com.fase.model.enums.Align;
import com.fase.model.enums.Size;
import com.google.gson.annotations.SerializedName;

public class Label extends VisualElement {

    @SerializedName("on_click")
    private Function onClick;
    private Align align;
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

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
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
