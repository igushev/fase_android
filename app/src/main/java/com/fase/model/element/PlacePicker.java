package com.fase.model.element;

import com.fase.model.data.Place;
import com.fase.model.enums.fase.PlacePickerType;
import com.fase.model.enums.fase.Size;

public class PlacePicker extends VisualElement {

    private Place place;
    private Size size;
    private PlacePickerType type;
    private String hint;

    public PlacePicker() {
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public PlacePickerType getType() {
        return type;
    }

    public void setType(PlacePickerType type) {
        this.type = type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
