package com.fase.model.element;

import com.fase.model.enums.fase.Size;

public class Web extends VisualElement {

    private String url;
    private Size size;
    private Boolean scrollable;

    public Web() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Boolean getScrollable() {
        return scrollable;
    }

    public void setScrollable(Boolean scrollable) {
        this.scrollable = scrollable;
    }
}
