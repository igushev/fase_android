package com.fase.core.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeSerializer implements JsonSerializer<Date> {

    private SimpleDateFormat pattern;

    public DateTimeSerializer(String pattern) {
        this.pattern = new SimpleDateFormat(pattern, Locale.getDefault());
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(pattern.format(src));
    }
}
