package com.fase.model.element;

import com.fase.model.data.Contact;
import com.fase.model.enums.Size;
import com.google.gson.annotations.SerializedName;

public class ContactPicker extends VisualElement {

    private Contact contact;
    @SerializedName("on_pick")
    private Function onPick;
    private Size size;
    private String hint;

    public ContactPicker() {
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Function getOnPick() {
        return onPick;
    }

    public void setOnPick(Function onPick) {
        this.onPick = onPick;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
