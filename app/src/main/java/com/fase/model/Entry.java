package com.fase.model;

import java.io.Serializable;

public class Entry<ID, VAL> implements Serializable {
    public ID id;
    public VAL name;

    public Entry() {
    }

    public Entry(ID id, VAL name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
