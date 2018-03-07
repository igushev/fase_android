package com.fase.model.enums.fase;

import com.google.gson.annotations.SerializedName;

public enum DateTimePickerType {

    @SerializedName("1")
    DATE(1),
    @SerializedName("2")
    TIME(2),
    @SerializedName("3")
    DATETIME(3);

    public final int value;

    DateTimePickerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
