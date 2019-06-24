package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("device_type")
    private String deviceType;
    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("device_token")
    private String deviceToken;
    @SerializedName("pixel_density")
    private Float pixelDensity;

    public Device() {
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Float getPixelDensity() {
        return pixelDensity;
    }

    public void setPixelDensity(Float pixelDensity) {
        this.pixelDensity = pixelDensity;
    }
}
