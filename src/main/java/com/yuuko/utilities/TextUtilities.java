package com.yuuko.utilities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class TextUtilities {

    /**
     * Replaces the last occurrence of a pattern with nothing.
     * @param stringBuilder {@link StringBuilder}
     * @param pattern String
     */
    public static void removeLast(StringBuilder stringBuilder, String pattern) {
        int index = stringBuilder.lastIndexOf(pattern);
        if(index > -1) {
            stringBuilder.replace(index, index + 1, "");
        }
    }

    /**
     * Converts a string to a boolean value, essentially just converting 1, true or yes to TRUE and anything else to FALSE.
     * @param value value to convert to boolean
     * @return boolean
     */
    public static boolean toBoolean(String value) {
        value = (value == null) ? "false" : value; // Stop null pointer exceptions.
        return value.equals("1") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes");
    }

    /**
     * Gets current songs timeStamp.
     * @param milliseconds long
     * @return formatted timeStamp.
     */
    public static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        int days    = (int) ((milliseconds / (1000 * 60 * 60 * 24)));

        if(days > 0) {
            return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        } else if(hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Gets verbose time string from time in milliseconds.
     * @param milliseconds long
     * @return formatted time string.
     */
    public static String getTimestampVerbose(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        int days    = (int) ((milliseconds / (1000 * 60 * 60 * 24)) % 7);
        int weeks   = (int) ((milliseconds / (1000 * 60 * 60 * 24 * 7)));

        if(weeks > 0) {
            return String.format("%02d weeks, %02d days, %02d hours, %02d minutes, %02d seconds.", weeks, days, hours, minutes, seconds);
        } else if(days > 0) {
            return String.format("%02d days, %02d hours, %02d minutes, %02d seconds.", days, hours, minutes, seconds);
        } else if(hours > 0) {
            return String.format("%02d hours, %02d minutes, %02d seconds.", hours, minutes, seconds);
        } else {
            return String.format("%02d minutes, %02d seconds.", minutes, seconds);
        }
    }

    /**
     * Formats the given time string into a dd/MM/yyyy string.
     * @param time String

     * @return formatted string.
     */
    public static String formatDate(String time) {
        Instant instant = Instant.parse(time.subSequence(0, time.length()));
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.UK).withZone(ZoneId.systemDefault()).format(instant);
    }

    /**
     * Rudimentary formatting for integers. (adding the ,'s)

     * @param integer integer to be formatted.
     * @return formatted string.
     */
    public static String formatInteger(String integer) {
        String[] splitInt = integer.split("");
        StringBuilder formattedInt = new StringBuilder();
        int counter = 0;

        for(int i = splitInt.length-1; i > -1; i--) {
            if(counter % 3 == 0 && counter != 0) {
                formattedInt.append(",");
            }
            formattedInt.append(splitInt[i]);
            counter++;
        }

        return formattedInt.reverse().toString();
    }

    /**
     * formatInteger flow controller
     * @param integer integer to be formatted.
     * @return formatted string
     */
    public static String formatInteger(int integer) {
        return formatInteger(integer + "");
    }
}
