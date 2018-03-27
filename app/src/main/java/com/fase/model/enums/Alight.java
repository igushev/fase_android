package com.fase.model.enums;

import com.google.gson.annotations.SerializedName;

public enum Alight {

    @SerializedName("1")
    LEFT(1),
    @SerializedName("2")
    RIGHT(2),
    @SerializedName("3")
    CENTER(3);

    public final int value;

    Alight(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
