package com.fase.model.element;

import java.util.List;

public class Tuple {

    private String elementId;
    private List<Element> elements;

    public Tuple() {
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
