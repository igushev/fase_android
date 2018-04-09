package com.fase.model.element;

import com.fase.model.enums.Align;

import java.util.List;

public class Select extends VisualElement {

    private List<String> items;
    private Align align;
    private String value;
    private String hint;

    public Select() {
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
