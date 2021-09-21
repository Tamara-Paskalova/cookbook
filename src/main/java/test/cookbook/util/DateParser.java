package test.cookbook.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateParser {
    private static final DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static LocalDateTime stringToDate(String date) {
        return LocalDateTime.parse(date, FORMATTER);
    }

    public static String dateToString(LocalDateTime date) {
        return date.format(FORMATTER);
    }
}
