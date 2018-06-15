package com.fase.model.element;

import com.google.gson.annotations.SerializedName;

public class Font {

    @SerializedName("size")
    private Float size;
    @SerializedName("bold")
    private Boolean bold;
    @SerializedName("italic")
    private Boolean italic;

    public Font() {
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public Boolean getItalic() {
        return italic;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }
}
