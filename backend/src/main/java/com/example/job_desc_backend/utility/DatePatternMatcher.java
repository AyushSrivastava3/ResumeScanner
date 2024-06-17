package com.example.job_desc_backend.utility;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePatternMatcher {

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2}))" +
                    "\\s*(?:to|–|-)\\s*" +
                    "((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2})|(?:Present)|(?:Till Date)|(?:Current)|(?:CURRENT))\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Map<String, Integer> MONTH_MAP = new HashMap<>();

    static {
        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] abbreviations = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String[] abbreviations1 = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] numericAbbreviations = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (int i = 0; i < months.length; i++) {
            MONTH_MAP.put(months[i].toLowerCase(), i + 1);
            MONTH_MAP.put(abbreviations[i].toLowerCase(), i + 1);
            MONTH_MAP.put(abbreviations1[i].toLowerCase(), i + 1);
            MONTH_MAP.put(numericAbbreviations[i], i + 1);
        }
    }

    public static Matcher getDateMatcher(String text) {
        return DATE_PATTERN.matcher(text);
    }

    public static LocalDate parseDate(String dateStr) {
        if ("Present".equalsIgnoreCase(dateStr) || "Till Date".equalsIgnoreCase(dateStr) || "Current".equalsIgnoreCase(dateStr) || "CURRENT".equalsIgnoreCase(dateStr)) {
            return LocalDate.now();
        }

        DateTimeFormatter formatter;
        if (dateStr.matches("\\d{1,2}-\\d{1,2}-\\d{2,4}") || dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{2,4}")) {
            formatter = DateTimeFormatter.ofPattern("[M/d/yyyy][M-d-yyyy][M/d/yy][M-d-yy]");
        } else if (dateStr.matches("\\d{1,2} \\w+ \\d{4}") || dateStr.matches("\\d{1,2}-\\w+-\\d{4}") || dateStr.matches("\\d{1,2} \\w+,? \\d{4}")) {
            formatter = DateTimeFormatter.ofPattern("[d MMMM yyyy][d-MMM-yyyy][d MMM, yyyy]");
        } else if (dateStr.matches("\\w+ \\d{4}") || dateStr.matches("\\w+-\\d{4}")) {
            formatter = DateTimeFormatter.ofPattern("[MMMM yyyy][MMM-yyyy]");
        } else if (dateStr.matches("\\d{1,2}/\\d{4}") || dateStr.matches("\\d{1,2}-\\d{4}")) {
            formatter = DateTimeFormatter.ofPattern("[M/yyyy][M-yyyy]");
        } else if (dateStr.matches("\\w{3}/\\d{4}")) {
            formatter = DateTimeFormatter.ofPattern("MMM/yyyy", Locale.ENGLISH);
        } else {
            formatter = DateTimeFormatter.ofPattern("[MMMM yyyy][MMM yyyy]", Locale.ENGLISH);
        }

        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            String[] parts = dateStr.split("[\\s/-]");
            int month = MONTH_MAP.getOrDefault(parts[0].toLowerCase(), 1);
            int year = Integer.parseInt(parts[parts.length - 1]);
            return LocalDate.of(year, month, 1);
        }
    }

    public static int calculateDurationInMonths(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }
}

