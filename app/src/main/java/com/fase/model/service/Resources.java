package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resources {

    @SerializedName("resource_list")
    private List<Resource> resourceList;

    public Resources() {
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }
}
