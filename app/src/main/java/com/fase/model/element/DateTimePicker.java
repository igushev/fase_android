package com.fase.model.element;

import com.fase.model.enums.DateTimePickerType;
import com.fase.model.enums.Size;

import java.util.Date;

public class DateTimePicker extends VisualElement {

    private Date datetime;
    private Size size;
    private DateTimePickerType type;
    private String hint;

    public DateTimePicker() {
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public DateTimePickerType getType() {
        return type;
    }

    public void setType(DateTimePickerType type) {
        this.type = type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
