package com.cs.harin.graph.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateFormatUtil {

    private static final String ZDT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final DateTimeFormatter ZDT_FORMATTER = DateTimeFormatter.ofPattern(ZDT_FORMAT);

    public static String zonedDateTimeToString(ZonedDateTime in) {
        return ZDT_FORMATTER.format(in);
    }
}
