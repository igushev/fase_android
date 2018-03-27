package com.fase.model.element;

import com.fase.model.enums.Alight;

import java.util.List;

public class Select extends VisualElement {

    private List<String> items;
    private Alight alight;
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

    public Alight getAlight() {
        return alight;
    }

    public void setAlight(Alight alight) {
        this.alight = alight;
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
