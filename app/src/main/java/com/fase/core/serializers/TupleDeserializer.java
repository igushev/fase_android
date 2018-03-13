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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import timber.log.Timber;

public class TupleDeserializer implements JsonDeserializer<Tuple> {

    @Override
    public Tuple deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Tuple tuple = new Tuple();
        try {
            JsonArray elementsArray = json.getAsJsonArray();
            if (elementsArray == null || elementsArray.size() == 0) {
                return null;
            }

            for (int i = 0; i < elementsArray.size(); i++) {
                if (i == 0) {
                    tuple.setElementId(elementsArray.get(0).getAsString());
                } else {
                    JsonObject object = elementsArray.get(i).getAsJsonObject();
                    String className = object.getAsJsonPrimitive("__class__").getAsString();
                    if (!TextUtils.isEmpty(className)) {
                        switch (className) {
                            case "Frame":
                                tuple.setElement(context.deserialize(object, Frame.class));
                                break;
                            case "Label":
                                tuple.setElement(context.deserialize(object, Label.class));
                                break;
                            case "Text":
                                tuple.setElement(context.deserialize(object, Text.class));
                                break;
                            case "Switch":
                                tuple.setElement(context.deserialize(object, Switch.class));
                                break;
                            case "Select":
                                tuple.setElement(context.deserialize(object, Select.class));
                                break;
                            case "Slider":
                                tuple.setElement(context.deserialize(object, Slider.class));
                                break;
                            case "Image":
                                tuple.setElement(context.deserialize(object, Image.class));
                                break;
                            case "Button":
                                tuple.setElement(context.deserialize(object, Button.class));
                                break;
                            case "DateTimePicker":
                                tuple.setElement(context.deserialize(object, DateTimePicker.class));
                                break;
                            case "PlacePicker":
                                tuple.setElement(context.deserialize(object, PlacePicker.class));
                                break;
                            case "Web":
                                tuple.setElement(context.deserialize(object, Web.class));
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return tuple;
    }
}
