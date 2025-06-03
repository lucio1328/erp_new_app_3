package com.lucio.erp_new_app_3.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class DateValidator {

    private static final DateTimeFormatter FORMAT_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final Pattern PATTERN_DDMMYYYY = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{4})$");
    private static final Pattern PATTERN_YYYYMMDD = Pattern.compile("^(\\d{4})/(\\d{2})/(\\d{2})$");

    public static boolean isValidDate(String dateStr) {
        return normalizeToStandardFormat(dateStr) != null;
    }

    public static LocalDate normalizeToStandardFormat(String dateStr) {
        try {
            if (PATTERN_DDMMYYYY.matcher(dateStr).matches()) {
                return validateAndParseDDMMYYYY(dateStr);
            }
            else if (PATTERN_YYYYMMDD.matcher(dateStr).matches()) {
                return validateAndParseYYYYMMDD(dateStr);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static LocalDate validateAndParseDDMMYYYY(String dateStr) {
        String[] parts = dateStr.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (!isValidDateComponents(year, month, day)) {
            return null;
        }
        return LocalDate.parse(dateStr, FORMAT_DDMMYYYY);
    }

    private static LocalDate validateAndParseYYYYMMDD(String dateStr) {
        String[] parts = dateStr.split("/");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (!isValidDateComponents(year, month, day)) {
            return null;
        }

        return LocalDate.parse(dateStr, FORMAT_YYYYMMDD);
    }

    private static boolean isValidDateComponents(int year, int month, int day) {
        if (month < 1 || month > 12 || day < 1) {
            return false;
        }

        int maxDay;
        switch (month) {
            case 2: maxDay = isLeapYear(year) ? 29 : 28; break;
            case 4: case 6: case 9: case 11: maxDay = 30; break;
            default: maxDay = 31;
        }

        return day <= maxDay;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}

