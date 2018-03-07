package com.fase.model.element;

import com.fase.model.enums.fase.Size;

public class Text extends VisualElement {

    private Size size;
    private String text;
    private String hint;

    public Text() {
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

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
