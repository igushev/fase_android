package com.fase.model.element;

import com.fase.model.enums.Align;

public class Image extends VisualElement {

    private String filename;
    private String url;
    private Align align;

    public Image() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }
}
