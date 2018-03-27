package com.fase.model.element;

import com.fase.model.enums.Size;
import com.fase.model.enums.Type;

public class Text extends VisualElement {

    private Size size;
    private String text;
    private String hint;
    private Type type;
    private Boolean multiline;

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean getMultiline() {
        return multiline;
    }

    public void setMultiline(Boolean multiline) {
        this.multiline = multiline;
    }
}
