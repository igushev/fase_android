package com.fase.model.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UpdateValueDbModel extends RealmObject {

    @PrimaryKey
    private String elementId;
    private RealmList<String> idList;
    private String value;

    public UpdateValueDbModel() {
    }

    public UpdateValueDbModel(String elementId, RealmList<String> idList, String value) {
        this.elementId = elementId;
        this.idList = idList;
        this.value = value;
    }

    public String getElementId() {
        return elementId;
    }

    public RealmList<String> getIdList() {
        return idList;
    }

    public String getValue() {
        return value;
    }
}
