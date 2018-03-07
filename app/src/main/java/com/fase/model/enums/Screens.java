package com.fase.model.enums;

public enum Screens {

    NONE(null);

    public final Class fragmentClass;

    Screens(Class fragmentClass) {
        this.fragmentClass = fragmentClass;
    }
}
