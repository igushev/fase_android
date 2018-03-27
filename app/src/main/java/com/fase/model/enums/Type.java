package com.fase.model.enums;

import com.google.gson.annotations.SerializedName;

public enum Type {

    @SerializedName("1")
    TEXT(1),
    @SerializedName("2")
    DIGITS(2),
    @SerializedName("3")
    PHONE(3),
    @SerializedName("4")
    EMAIL(4);

    public final int value;

    Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
