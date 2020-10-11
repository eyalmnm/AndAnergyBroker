package com.usenergysolutions.energybroker.utils;


import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Locale;

// https://stackoverflow.com/questions/17098329/add-leading-zeroes-to-a-string

public class StringUtils {

    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * Check whether the given string is null or empty
     *
     * @param str the string to be checked
     * @return true if the given string in null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null)
            return true;
        str = str.replaceAll("/?", "").replaceAll("<", "").replaceAll(">", "")
                .replaceAll("&", "").replaceAll("\"", "").replaceAll("\'", "")
                .replaceAll(";", "").replaceAll("\n", "").replaceAll("\r", "")
                .replaceAll("\t", "").trim();
        return str.trim().length() == 0;
    }

    public static boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone.isEmpty()) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    public static boolean containsIgnureCase(String container, String content) {
        return container.toLowerCase().contains(content.toLowerCase());
    }

    public static int passwordStrengthCaculation(String password) {
        String temp = password;
        int length = 0, uppercase = 0, lowercase = 0, digits = 0, symbols = 0, bonus = 0, requirements = 0;
        int lettersOnly = 0, numbersOnly = 0, cuc = 0, clc = 0;

        length = temp.length();
        for (int i = 0; i < temp.length(); i++) {
            if (Character.isUpperCase(temp.charAt(i)))
                uppercase++;
            else if (Character.isLowerCase(temp.charAt(i)))
                lowercase++;
            else if (Character.isDigit(temp.charAt(i)))
                digits++;
            symbols = length - uppercase - lowercase - digits;
        }

        for (int j = 1; j < temp.length() - 1; j++) {
            if (Character.isDigit(temp.charAt(j)))
                bonus++;
        }

        for (int k = 0; k < temp.length(); k++) {
            if (Character.isUpperCase(temp.charAt(k))) {
                k++;
                if (k < temp.length()) {
                    if (Character.isUpperCase(temp.charAt(k))) {
                        cuc++;
                        k--;
                    }
                }
            }
        }

        for (int l = 0; l < temp.length(); l++) {
            if (Character.isLowerCase(temp.charAt(l))) {
                l++;
                if (l < temp.length()) {
                    if (Character.isLowerCase(temp.charAt(l))) {
                        clc++;
                        l--;
                    }
                }
            }
        }

        if (length > 7) {
            requirements++;
        }

        if (uppercase > 0) {
            requirements++;
        }

        if (lowercase > 0) {
            requirements++;
        }

        if (digits > 0) {
            requirements++;
        }

        if (symbols > 0) {
            requirements++;
        }

        if (bonus > 0) {
            requirements++;
        }

        if (digits == 0 && symbols == 0) {
            lettersOnly = 1;
        }

        if (lowercase == 0 && uppercase == 0 && symbols == 0) {
            numbersOnly = 1;
        }

        int total = (length * 4) + ((length - uppercase) * 2)
                + ((length - lowercase) * 2) + (digits * 4) + (symbols * 6)
                + (bonus * 2) + (requirements * 2) - (lettersOnly * length * 2)
                - (numbersOnly * length * 3) - (cuc * 2) - (clc * 2);
        return total;
    }

    public static String placeTypeAndTimeCleaner(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '"') {
                continue;
            } else {
                if (c == '_') {
                    c = ' ';
                }
            }
            builder.append(c);
        }
        return builder.toString().trim();
    }


    public static String stringArrayToString(String[] strings) {
        return stringArrayToString(strings, ",");
    }

    public static String stringArrayToString(String[] strings, String delimiter) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            if (!strings[i].isEmpty()) {  // Don't add empty spaces follow with delimiters
                sb.append(strings[i]).append(delimiter);
            }
        }

        String retString = sb.toString();
        if (retString.endsWith(delimiter)) {
            retString = retString.substring(0, retString.lastIndexOf(delimiter));
        }

        return retString;
    }

    public static String[] stringToStringArray(String str) {
        return stringToStringArray(str, ",");
    }

    public static String[] stringToStringArray(String str, String delimiter) {
        return str.split(delimiter);
    }

    public static String replaceAll(String str, char target, char destination) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == target) {
                c = destination;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean isNumber(String text) {
        return text.matches("[0-9]+");
    }

    public static String convertToTwoDigits(int number) {
        return String.format("%02d", number);
    }

    public static String getDateTimeString(long currentMillis) {
        return dateTimeFormat.format(currentMillis);
    }
}
