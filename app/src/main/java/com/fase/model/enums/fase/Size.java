package com.fase.model.enums.fase;

import com.google.gson.annotations.SerializedName;

public enum Size {

    @SerializedName("1")
    MIN(1),
    @SerializedName("2")
    MAX(2);

    public final int value;

    Size(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
