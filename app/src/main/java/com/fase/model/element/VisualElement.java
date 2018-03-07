package com.fase.model.element;

import com.fase.model.data.Locale;
import com.google.gson.annotations.SerializedName;

public class VisualElement extends ElementContainer {

    protected Boolean displayed;
    protected Locale locale;
    @SerializedName("request_locale")
    protected Boolean requestLocale;

    public VisualElement() {
    }

    public Boolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(Boolean displayed) {
        this.displayed = displayed;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Boolean getRequestLocale() {
        return requestLocale;
    }

    public void setRequestLocale(Boolean requestLocale) {
        this.requestLocale = requestLocale;
    }
}
