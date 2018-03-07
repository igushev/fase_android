package com.fase.model.element;

import com.fase.model.enums.fase.Alight;

import java.util.List;

public class Select extends VisualElement {

    private List<String> items;
    private Alight alight;
    private String value;
    private String text;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
