package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

public class Slider extends VisualElement {

    private Float value;
    @SerializedName("min_value")
    private Float minValue;
    @SerializedName("max_value")
    private Float maxValue;
    private Float step;

    public Slider() {
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Float getMinValue() {
        return minValue;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }

    public Float getStep() {
        return step;
    }

    public void setStep(Float step) {
        this.step = step;
    }
}
