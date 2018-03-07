package com.fase.model.element;

import com.fase.model.enums.fase.Orientation;
import com.fase.model.enums.fase.Size;
import com.google.gson.annotations.SerializedName;

public class Frame extends BaseElementsContainer {

    @SerializedName("on_click")
    private Function onClick;
    private Orientation orientation;
    private Boolean border;
    private Size size;

    public Frame() {
    }

    public Function getOnClick() {
        return onClick;
    }

    public void setOnClick(Function onClick) {
        this.onClick = onClick;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Boolean getBorder() {
        return border;
    }

    public void setBorder(Boolean border) {
        this.border = border;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
