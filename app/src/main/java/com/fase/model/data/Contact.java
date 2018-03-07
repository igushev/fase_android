package com.fase.model.data;

import com.google.gson.annotations.SerializedName;

public class Contact {

    @SerializedName("display_name")
    private String displayName;
    @SerializedName("phone_number")
    private String phoneNumber;

    public Contact() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
