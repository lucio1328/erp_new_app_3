package com.lucio.erp_new_app_3.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

@Component
public class DateValidator {
    public static boolean isValidDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            String[] parts = dateStr.split("/");
            if (parts.length != 3) return false;

            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();

            if (month < 1 || month > 12) return false;
            if (day < 1) return false;

            int maxDay;
            switch (month) {
                case 2:
                    maxDay = isLeapYear(year) ? 29 : 28;
                    break;
                case 4: case 6: case 9: case 11:
                    maxDay = 30;
                    break;
                default:
                    maxDay = 31;
            }

            return day <= maxDay;
        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
