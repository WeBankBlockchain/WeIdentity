/*
 *       Copyright© (2018) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bcos.web3j.abi.datatypes.generated.Int256;

/**
 * Date formatter utilities.
 *
 * @author lingfenghe
 */
public class DateUtils {

    /**
     * Get the ISO8601 timestamp.
     *
     * @param date the date
     * @return the ISO 8601 timestamp
     */
    public static String getTimestamp(Date date) {
        return getDefaultDateFormat().format(date);
    }

    /**
     * Gets the ISO 8601 timestamp.
     *
     * @param date the date
     * @return the ISO 8601 timestamp
     */
    public static String getTimestamp(long date) {
        return getDefaultDateFormat().format(date);
    }

    /**
     * Gets the default date format.
     *
     * @return the default date format
     */
    private static DateFormat getDefaultDateFormat() {
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df;
    }

    /**
     * Convert a String to Date based on a specific DateFormat.
     *
     * @param dateString the date string
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date convertStringToDate(String dateString) throws ParseException {
        return getDefaultDateFormat().parse(dateString);
    }

    /**
     * Convert long string to date.
     *
     * @param dateString the date string
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date convertLongStringToDate(String dateString) throws ParseException {
        return new Date(Long.parseLong(dateString));
    }

    /**
     * Conver date to time stamp.
     *
     * @param time the time
     * @return the long
     * @throws ParseException the parse exception
     */
    public static long converDateToTimeStamp(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(time);
        long ts = date.getTime();
        return ts;
    }

    /**
     * Check the UTC format validity of a Date String.
     *
     * @param dateString the date string
     * @return true, if is valid UTC date string
     */
    public static boolean isValidDateString(String dateString) {
        try {
            DateFormat df = getDefaultDateFormat();
            df.setLenient(false);
            df.parse(dateString);
            return true;
        } catch (ParseException parseEx) {
            return false;
        }
    }

    /**
     * Get current timestamp in Int256 type.
     *
     * @return the current time stamp int 256
     */
    public static Int256 getCurrentTimeStampInt256() {
        return new Int256(System.currentTimeMillis());
    }

    /**
     * Get current timestamp in String type.
     *
     * @return the current time stamp string
     */
    public static String getCurrentTimeStampString() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     *  Get current timestamp in String type.
     *
     * @return the current time stamp long
     */
    public static Long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }
}
