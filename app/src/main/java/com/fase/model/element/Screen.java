package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

public class Screen extends BaseElementsContainer {

    @SerializedName("on_refresh")
    private Function onRefresh;
    private String title;
    @SerializedName("_screen_id")
    private String screenId;
    private Boolean scrollable;
    @SerializedName("on_more")
    private Function onMore;

    public Screen() {
    }

    public Function getOnRefresh() {
        return onRefresh;
    }

    public void setOnRefresh(Function onRefresh) {
        this.onRefresh = onRefresh;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public Boolean getScrollable() {
        return scrollable;
    }

    public void setScrollable(Boolean scrollable) {
        this.scrollable = scrollable;
    }

    public Function getOnMore() {
        return onMore;
    }

    public void setOnMore(Function onMore) {
        this.onMore = onMore;
    }
}
