package com.fase.core.serializers;

import com.fase.model.element.Element;
import com.fase.model.element.Tuple;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TupleSerializer implements JsonSerializer<Tuple> {

    @Override
    public JsonElement serialize(Tuple src, Type typeOfSrc, JsonSerializationContext context) {
        if (src != null) {
            JsonArray elements = new JsonArray();
            elements.add(src.getElementId());
            if (src.getElements() != null && !src.getElements().isEmpty()) {
                for (Element element : src.getElements()) {
                    elements.add(context.serialize(element));
                }
            }
            return elements;
        }
        return null;
    }
}
