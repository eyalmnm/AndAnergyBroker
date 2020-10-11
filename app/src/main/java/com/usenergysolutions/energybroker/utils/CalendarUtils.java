package com.usenergysolutions.energybroker.utils;

import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// Ref: https://stackoverflow.com/a/20119571
// Ref: https://stackoverflow.com/questions/18256521/android-calendar-get-current-day-of-week-as-string
// Ref: https://stackoverflow.com/questions/8573250/android-how-can-i-convert-string-to-date
// Ref: https://stackoverflow.com/questions/17677546/convert-utc-into-local-time-on-android

public class CalendarUtils {
    private static final String TAG = "CalendarUtils";

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static SimpleDateFormat dateTimeFormat12 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.US);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

    public static String getDayOfTheWeekName() {
        String weekDay;

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());

        return weekDay;
    }

    public static String getDayOfTheWeekName(long millis) {
        String weekDay;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        weekDay = dayFormat.format(calendar.getTime());

        return weekDay;
    }

    public static long getCurrentLocalTimeLong() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Log.d("CalendarUtils", " current time: " + dateTimeFormat.format(currentTime));
        return currentTime;
    }

    public static String getCurrentLocalTimeString() {
        return getCurrentLocalTimeString(getCurrentLocalTimeLong());
    }

    public static String getCurrentLocalTimeString(long time) {
        String formattedLocalTime = dateTimeFormat.format(time);
        Log.d(TAG, "getCurrentLocalTimeString current time : " + formattedLocalTime);
        return formattedLocalTime;
    }

    public static String getCurrentLocalTimeString12H(long time) {
        String formattedLocalTime = dateTimeFormat12.format(time);
        Log.d(TAG, "getCurrentLocalTimeString current time : " + formattedLocalTime);
        return formattedLocalTime;
    }

    public static String getCurrentLocalTime12HString(long time) {
        String formattedLocalTime = dateTimeFormat12.format(time);
        Log.d(TAG, "getCurrentLocalTimeString current time : " + formattedLocalTime);
        return formattedLocalTime;

    }

    public static String getCurrentLocalDateString() {
        return getCurrentLocalDateString(getCurrentLocalTimeLong());
    }

    public static String getCurrentLocalDateString(long time) {
        String formattedLocalTime = dateFormat.format(time);
        Log.d(TAG, "getCurrentLocalTimeString current time : " + formattedLocalTime);
        return formattedLocalTime;
    }


    public static Date stringToDate(String dateStr, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "stringToDate failed to parse date: " + dateStr, e);
        }
        return null;
    }

    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    @NotNull
    public static String convertTo12H(@NotNull String dateTimeStr) {
        long millis = getDateTimeMillis(dateTimeStr);
        return getCurrentLocalTime12HString(millis);
    }

    public static long getDateTimeMillis(String dateTimeStr) {
        Date newDate = new Date();
        try {
            newDate = dateTimeFormat.parse(dateTimeStr);
        } catch (ParseException e) {
        }
        return newDate.getTime();
    }


    // https://stackoverflow.com/questions/17677546/convert-utc-into-local-time-on-android
    public static String convertUtcToLocal12H(String utcTimeStr) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = df.parse(utcTimeStr);
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

}
