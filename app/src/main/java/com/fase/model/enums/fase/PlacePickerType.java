package com.fase.model.enums.fase;

import com.google.gson.annotations.SerializedName;

public enum PlacePickerType {

    @SerializedName("1")
    CITY(1);

    public final int value;

    PlacePickerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
