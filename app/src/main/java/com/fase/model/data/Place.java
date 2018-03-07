package com.fase.model.data;

import com.google.gson.annotations.SerializedName;

public class Place {

    @SerializedName("google_place_id")
    private String googlePlaceId;
    private String country;
    private String state;
    private String city;

    public Place() {
    }

    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
