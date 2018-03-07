package com.fase.model.element;

import com.fase.model.enums.fase.DateTimePickerType;
import com.fase.model.enums.fase.Size;

import java.util.Date;

public class DateTimePicker extends VisualElement {

    private Date date;
    private Size size;
    private DateTimePickerType type;
    private String hint;

    public DateTimePicker() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
