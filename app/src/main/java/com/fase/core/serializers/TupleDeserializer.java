package com.fase.core.serializers;

import android.text.TextUtils;

import com.fase.model.element.Button;
import com.fase.model.element.DateTimePicker;
import com.fase.model.element.Frame;
import com.fase.model.element.Image;
import com.fase.model.element.Label;
import com.fase.model.element.PlacePicker;
import com.fase.model.element.Select;
import com.fase.model.element.Slider;
import com.fase.model.element.Switch;
import com.fase.model.element.Text;
import com.fase.model.element.Tuple;
import com.fase.model.element.Web;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TupleDeserializer implements JsonDeserializer<Tuple> {

    @Override
    public Tuple deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray elementsArray = json.getAsJsonArray();
        if (elementsArray == null || elementsArray.size() == 0) {
            return null;
        }
        Gson gson = new Gson();
        Tuple tuple = new Tuple();
        tuple.setElements(new ArrayList<>());
        for (int i = 0; i < elementsArray.size(); i++) {
            if (i == 0) {
                tuple.setElementId(elementsArray.get(i).getAsString());
            } else {
                JsonObject object = elementsArray.get(i).getAsJsonObject();
                String className = object.getAsJsonPrimitive("__class__").getAsString();
                if (!TextUtils.isEmpty(className)) {
                    switch (className) {
                        case "Frame":
                            tuple.getElements().add(gson.fromJson(object, Frame.class));
                            break;
                        case "Label":
                            tuple.getElements().add(gson.fromJson(object, Label.class));
                            break;
                        case "Text":
                            tuple.getElements().add(gson.fromJson(object, Text.class));
                            break;
                        case "Switch":
                            tuple.getElements().add(gson.fromJson(object, Switch.class));
                            break;
                        case "Select":
                            tuple.getElements().add(gson.fromJson(object, Select.class));
                            break;
                        case "Slider":
                            tuple.getElements().add(gson.fromJson(object, Slider.class));
                            break;
                        case "Image":
                            tuple.getElements().add(gson.fromJson(object, Image.class));
                            break;
                        case "Button":
                            tuple.getElements().add(gson.fromJson(object, Button.class));
                            break;
                        case "DateTimePicker":
                            tuple.getElements().add(gson.fromJson(object, DateTimePicker.class));
                            break;
                        case "PlacePicker":
                            tuple.getElements().add(gson.fromJson(object, PlacePicker.class));
                            break;
                        case "Web":
                            tuple.getElements().add(gson.fromJson(object, Web.class));
                            break;
                    }
                }
            }
        }
        return tuple;
    }
}
