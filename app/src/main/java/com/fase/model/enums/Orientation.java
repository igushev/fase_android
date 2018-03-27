package com.fase.model.enums;

import com.google.gson.annotations.SerializedName;

public enum Orientation {

    @SerializedName("1")
    VERTICAL(1),
    @SerializedName("2")
    HORIZONTAL(2);

    public final int value;

    Orientation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
