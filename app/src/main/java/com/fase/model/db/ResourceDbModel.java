package com.fase.model.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ResourceDbModel extends RealmObject {

    @PrimaryKey
    private String fileName;
    private String filePath;

    public ResourceDbModel() {
    }

    public ResourceDbModel(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
}
