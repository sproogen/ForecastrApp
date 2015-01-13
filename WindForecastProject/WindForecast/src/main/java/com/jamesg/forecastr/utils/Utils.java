package com.jamesg.forecastr.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Android Log wrapper class that can use {@link String#format(String, Object...)} in logging message
 */
public class Utils {

    /**
     * Get a Calendar from a date stamp
     * @param timestamp
     * @return
     */
    public static Calendar calendarFromDateStamp(String timestamp) {
        Date d = stringToDate(timestamp, "yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        //I don't think we need this next line as I now think this is an error with the server not updating the time stamps correctly.
        //c.add(Calendar.DAY_OF_MONTH, -1);
        return c;
    }

    public static boolean calendarsAreSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static int calendarsGetDiffInDays(Calendar c1, Calendar c2) {
        long ms1 = c1.getTimeInMillis();
        long ms2 = c2.getTimeInMillis();
        long diff = ms2 - ms1;
        int diffInDays = (int) (diff / (24 * 60 * 60 * 1000));
        return diffInDays;
    }

    public static Calendar removeTimeFromCalendar(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    private static Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }
}