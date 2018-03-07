package com.fase.model.data;

import com.google.gson.annotations.SerializedName;

public class Locale {

    @SerializedName("country_code")
    private String countryCode;

    public Locale() {
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
