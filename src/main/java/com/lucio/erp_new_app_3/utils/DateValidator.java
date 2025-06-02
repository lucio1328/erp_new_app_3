package com.lucio.erp_new_app_3.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class DateValidator {

    private static final DateTimeFormatter FORMAT_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final Pattern PATTERN_DDMMYYYY = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final Pattern PATTERN_YYYYMMDD = Pattern.compile("^\\d{4}/\\d{2}/\\d{2}$");

    public static boolean isValidDate(String dateStr) {
        return normalizeToStandardFormat(dateStr) != null;
    }

    public static LocalDate normalizeToStandardFormat(String dateStr) {
        LocalDate date;

        try {
            if (PATTERN_DDMMYYYY.matcher(dateStr).matches()) {
                date = LocalDate.parse(dateStr, FORMAT_DDMMYYYY);
            }
            else if (PATTERN_YYYYMMDD.matcher(dateStr).matches()) {
                date = LocalDate.parse(dateStr, FORMAT_YYYYMMDD);
            }
            else {
                return null;
            }

            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();

            if (month < 1 || month > 12 || day < 1) return null;

            int maxDay;
            switch (month) {
                case 2: maxDay = isLeapYear(year) ? 29 : 28; break;
                case 4: case 6: case 9: case 11: maxDay = 30; break;
                default: maxDay = 31;
            }

            if (day > maxDay) return null;

            return LocalDate.parse(date.format(FORMAT_YYYYMMDD), FORMAT_YYYYMMDD);

        }
        catch (DateTimeParseException | NumberFormatException e) {
            return null;
        }
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}

