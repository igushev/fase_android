package com.fase.core.serializers;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

    private static DateTimeFormatter ISOWithoutMillisOrOffsetWithTimezone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static DateTimeFormatter ISOWithoutMillisOrOffset = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static DateTimeFormatter dateAndTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateAndTimeFormat1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    private static DateTimeFormatter dateOnlyFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter dateOnlySlashedFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
    private static DateTimeFormatter dateTimeAmPmFormat = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss a");

    private String predefinedPatter;

    public DateTimeDeserializer(String predefinedPatter) {
        this.predefinedPatter = predefinedPatter;
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final String date = json.getAsJsonPrimitive().getAsString();

        if (TextUtils.isEmpty(date)) {
            return null;
        }

        if (TextUtils.isEmpty(predefinedPatter)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(predefinedPatter);
            return dateTimeFormatter.parseDateTime(date).toDate();
        }
        // try parse yyyy-MM-dd hh:mm:ss a format
        try {
            DateTime dateTime = dateTimeAmPmFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse via stock DateFormat class
        try {
            DateFormat dateFormat = DateFormat.getInstance();
            return dateFormat.parse(date);
        } catch (ParseException ignored) {
        }
        // try parse yyyy-mm-dd format
        try {
            DateTime dateTime = dateOnlyFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse yyyy-mm-dd hh:mm:ss format
        try {
            DateTime dateTime = dateAndTimeFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse yyyy-mm-dd hh:mm format
        try {
            DateTime dateTime = dateAndTimeFormat1.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse dd/MM/yyyy format
        try {
            DateTime dateTime = dateOnlySlashedFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse ISO date
        try {
            DateTime dateTime = ISOWithoutMillisOrOffset.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse yyyy-MM-dd'T'HH:mm:ssZ format
        try {
            DateTime dateTime = ISOWithoutMillisOrOffsetWithTimezone.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception ignored) {
        }
        // try parse ISO date
        try {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser()
                    .withOffsetParsed()
                    .parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            Timber.e("Error parsing date");
        }
        return null;
    }
}
