package com.fase.util;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class DateTimeUtil {

    public static final String APP_DATE_FORMAT = "dd.MM.yyyy";
    public static final String APP_TIME_FORMAT = "HH:mm";

    private static DateTimeFormatter mISOWithoutMillisOrOffsetWithTimezone = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static DateTimeFormatter mISOWithoutMillisOrOffset = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static DateTimeFormatter mDateAndTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter mDateAndTimeFormat1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    private static DateTimeFormatter mDateOnlyFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter mDateOnlySlashedFormat = DateTimeFormat.forPattern("dd/MM/yyyy");

    public static String formatDate(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            return format.format(date);
        }
        return null;
    }

    public static Date formatDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return tryParseDate(date);
        }
    }

    public static String convertDateFormat(String date, String oldFormatPattern, String newFormatPattern) {
        String result = date;
        SimpleDateFormat oldFormat = new SimpleDateFormat(oldFormatPattern, Locale.getDefault());
        SimpleDateFormat newFormat = new SimpleDateFormat(newFormatPattern, Locale.getDefault());
        newFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date parsedDate = oldFormat.parse(date);
            result = newFormat.format(parsedDate);
        } catch (ParseException e) {
            Timber.e(e.getMessage());
        }
        return result;
    }

    private static Date tryParseDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return null;
        }
        // try parse via stock DateFormat class
        try {
            DateFormat dateFormat = DateFormat.getInstance();
            return dateFormat.parse(date);
        } catch (ParseException e) {
            // ignore and try next
        }
        // try parse yyyy-mm-dd format
        try {
            DateTime dateTime = mDateOnlyFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            // ignore and try next
        }
        // try parse yyyy-mm-dd hh:mm:ss format
        try {
            DateTime dateTime = mDateAndTimeFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            // ignore and try next
        }
        // try parse yyyy-mm-dd hh:mm format
        try {
            DateTime dateTime = mDateAndTimeFormat1.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            // ignore and try next
        }
        // try parse dd/MM/yyyy format
        try {
            DateTime dateTime = mDateOnlySlashedFormat.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            // ignore and try next
        }
        // try parse ISO date
        try {
            DateTime dateTime = mISOWithoutMillisOrOffset.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            // ignore and try next
        }
        // try parse yyyy-MM-dd'T'HH:mm:ssZ format
        try {
            DateTime dateTime = mISOWithoutMillisOrOffsetWithTimezone.parseDateTime(date);
            return dateTime.toDate();
        } catch (Exception e) {
            // ignore and try next
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
