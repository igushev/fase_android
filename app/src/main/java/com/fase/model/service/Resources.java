package com.fase.model.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resources {

    @SerializedName("resource_list")
    private List<Resource> resourceList;
    @SerializedName("reset_resources")
    private boolean resetResources;

    public Resources() {
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public boolean isResetResources() {
        return resetResources;
    }

    public void setResetResources(boolean resetResources) {
        this.resetResources = resetResources;
    }
}
