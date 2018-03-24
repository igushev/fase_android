package com.fase.core.serializers;

import android.text.TextUtils;

import com.fase.model.element.Alert;
import com.fase.model.element.Button;
import com.fase.model.element.ContactPicker;
import com.fase.model.element.DateTimePicker;
import com.fase.model.element.Frame;
import com.fase.model.element.Image;
import com.fase.model.element.Label;
import com.fase.model.element.Menu;
import com.fase.model.element.MenuItem;
import com.fase.model.element.Navigation;
import com.fase.model.element.PlacePicker;
import com.fase.model.element.Select;
import com.fase.model.element.Separator;
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

public class TupleDeserializer implements JsonDeserializer<Tuple> {

    @Override
    public Tuple deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray elementsArray = json.getAsJsonArray();
        if (elementsArray == null || elementsArray.size() == 0) {
            return null;
        }
        Tuple tuple = new Tuple();
        JsonElement nameElement = elementsArray.get(0);
        if (nameElement != null) {
            tuple.setElementId(nameElement.getAsString());
        }
        if (elementsArray.size() < 2) {
            return tuple;
        }
        JsonElement viewElement = elementsArray.get(1);
        if (viewElement != null) {
            JsonObject object = viewElement.getAsJsonObject();
            String className = object.getAsJsonPrimitive("__class__").getAsString();
            if (!TextUtils.isEmpty(className)) {
                switch (className) {
                    case "Frame":
                        tuple.setElement(context.deserialize(viewElement, Frame.class));
                        break;
                    case "Label":
                        tuple.setElement(context.deserialize(viewElement, Label.class));
                        break;
                    case "Text":
                        tuple.setElement(context.deserialize(viewElement, Text.class));
                        break;
                    case "Switch":
                        tuple.setElement(context.deserialize(viewElement, Switch.class));
                        break;
                    case "Select":
                        tuple.setElement(context.deserialize(viewElement, Select.class));
                        break;
                    case "Slider":
                        tuple.setElement(context.deserialize(viewElement, Slider.class));
                        break;
                    case "Image":
                        tuple.setElement(context.deserialize(viewElement, Image.class));
                        break;
                    case "Button":
                        tuple.setElement(context.deserialize(viewElement, Button.class));
                        break;
                    case "DateTimePicker":
                        tuple.setElement(context.deserialize(viewElement, DateTimePicker.class));
                        break;
                    case "PlacePicker":
                        tuple.setElement(context.deserialize(viewElement, PlacePicker.class));
                        break;
                    case "Web":
                        tuple.setElement(context.deserialize(viewElement, Web.class));
                        break;
                    case "ContactPicker":
                        tuple.setElement(context.deserialize(viewElement, ContactPicker.class));
                        break;
                    case "Menu":
                        tuple.setElement(context.deserialize(viewElement, Menu.class));
                        break;
                    case "MenuItem":
                        tuple.setElement(context.deserialize(viewElement, MenuItem.class));
                        break;
                    case "Separator":
                        tuple.setElement(context.deserialize(viewElement, Separator.class));
                        break;
                    case "Navigation":
                        tuple.setElement(context.deserialize(viewElement, Navigation.class));
                        break;
                    case "Alert":
                        tuple.setElement(context.deserialize(viewElement, Alert.class));
                        break;
                }
            }
        }
        return tuple;
    }
}
