package com.fase.model.data;

import com.google.gson.annotations.SerializedName;

public class Locale {

    @SerializedName("country_code")
    private String countryCode;

    public Locale() {
    }

    public Locale(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
