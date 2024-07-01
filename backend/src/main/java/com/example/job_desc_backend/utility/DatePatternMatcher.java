package com.example.job_desc_backend.utility;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePatternMatcher {

//    private static final Pattern DATE_PATTERN = Pattern.compile(
//            "\\b((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2}))" +
//                    "\\s*(?:to|–|-)\\s*" +
//                    "((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2})|(?:Present)|(?:Till Date)|(?:Current)|(?:CURRENT))\\b",
//            Pattern.CASE_INSENSITIVE
//    );

//    private static final Pattern DATE_PATTERN = Pattern.compile(
//            "\\b((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2})|(?:\\d{2}/\\d{2}/\\d{4})|(?:\\d{2}-\\d{2}-\\d{4}))" +
//                    "\\s*(?:to|–|-)\\s*" +
//                    "((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2})|(?:\\d{2}/\\d{2}/\\d{4})|(?:\\d{2}-\\d{2}-\\d{4})|(?:Present)|(?:Till Date)|(?:Current)|(?:CURRENT))\\b",
//            Pattern.CASE_INSENSITIVE
//    );


    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b((?:\\d{1,2}/\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{1,2}-\\d{4})|(?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2}))" +
                    "\\s*(?:to|–|-)\\s*" +
                    "((?:\\d{1,2}/\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{1,2}-\\d{4})|(?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\w+ \\d{2})|(?:\\w+-\\d{2})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}-\\d{4})|(?:\\d{1,2}/\\d{2,4})|(?:\\d{1,2}-\\d{2,4})|(?:\\d{4})|(?:\\w{3}/\\d{4})|(?:\\d{1,2} \\w+ \\d{4})|(?:\\d{1,2}-\\w+-\\d{4})|(?:\\d{1,2} \\w+,? \\d{4})|(?:\\d{1,2} \\w+ \\d{2})|(?:Present)|(?:Till Date)|(?:Current)|(?:CURRENT))\\b",
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


    //old parsing code

//    public static LocalDate parseDate(String dateStr) {
//        if ("Present".equalsIgnoreCase(dateStr) || "Till Date".equalsIgnoreCase(dateStr) || "Current".equalsIgnoreCase(dateStr) || "CURRENT".equalsIgnoreCase(dateStr)) {
//            return LocalDate.now();
//        }
//
//        DateTimeFormatter formatter;
//        if (dateStr.matches("\\d{1,2}-\\d{1,2}-\\d{2,4}") || dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{2,4}")) {
//            formatter = DateTimeFormatter.ofPattern("[M/d/yyyy][M-d-yyyy][M/d/yy][M-d-yy]");
//        } else if (dateStr.matches("\\d{1,2} \\w+ \\d{4}") || dateStr.matches("\\d{1,2}-\\w+-\\d{4}") || dateStr.matches("\\d{1,2} \\w+,? \\d{4}")) {
//            formatter = DateTimeFormatter.ofPattern("[d MMMM yyyy][d-MMM-yyyy][d MMM, yyyy]");
//        } else if (dateStr.matches("\\w+ \\d{4}") || dateStr.matches("\\w+-\\d{4}")) {
//            formatter = DateTimeFormatter.ofPattern("[MMMM yyyy][MMM-yyyy]");
//        } else if (dateStr.matches("\\d{1,2}/\\d{4}") || dateStr.matches("\\d{1,2}-\\d{4}")) {
//            formatter = DateTimeFormatter.ofPattern("[M/yyyy][M-yyyy]");
//        } else if (dateStr.matches("\\w{3}/\\d{4}")) {
//            formatter = DateTimeFormatter.ofPattern("MMM/yyyy", Locale.ENGLISH);
//        } else {
//            formatter = DateTimeFormatter.ofPattern("[MMMM yyyy][MMM yyyy]", Locale.ENGLISH);
//        }
//
//        try {
//            return LocalDate.parse(dateStr, formatter);
//        } catch (Exception e) {
//            String[] parts = dateStr.split("[\\s/-]");
//            int month = MONTH_MAP.getOrDefault(parts[0].toLowerCase(), 1);
//            int year = Integer.parseInt(parts[parts.length - 1]);
//            return LocalDate.of(year, month, 1);
//        }
//    }
    //old parsing code ends here
    // new code for fixing bugs

//    public static LocalDate parseDate(String dateStr) {
//        if ("Present".equalsIgnoreCase(dateStr) || "Till Date".equalsIgnoreCase(dateStr) || "Current".equalsIgnoreCase(dateStr) || "CURRENT".equalsIgnoreCase(dateStr)) {
//            return LocalDate.now();
//        }
//
//        DateTimeFormatter primaryFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
//        DateTimeFormatter[] alternativeFormatters = new DateTimeFormatter[]{
//                DateTimeFormatter.ofPattern("M-d-yyyy"),
//                DateTimeFormatter.ofPattern("M/d/yy"),
//                DateTimeFormatter.ofPattern("M-d-yy"),
//                DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH),
//                DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH),
//                DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.ENGLISH),
//                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH),
//                DateTimeFormatter.ofPattern("MMM-yyyy", Locale.ENGLISH),
//                DateTimeFormatter.ofPattern("M/yyyy"),
//                DateTimeFormatter.ofPattern("M-yyyy"),
//                DateTimeFormatter.ofPattern("MMM/yyyy", Locale.ENGLISH),
//                DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)
//        };
//
//        // Preprocess to ensure correct day and month order
//        String preprocessedDateStr = preprocessDate(dateStr);
//
//        try {
//            return LocalDate.parse(preprocessedDateStr, primaryFormatter);
//        } catch (DateTimeParseException e) {
//            for (DateTimeFormatter formatter : alternativeFormatters) {
//                try {
//                    return LocalDate.parse(preprocessedDateStr, formatter);
//                } catch (DateTimeParseException ignored) {
//                }
//            }
//        }
//
//        try {
//            String[] parts = dateStr.split("[\\s/-]");
//            int month = MONTH_MAP.getOrDefault(parts[0].toLowerCase(), 1);
//            int year = Integer.parseInt(parts[parts.length - 1]);
//            return LocalDate.of(year, month, 1);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Unable to parse date: " + dateStr, e);
//        }
//    }
//
//    private static String preprocessDate(String dateStr) {
//        if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}") || dateStr.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
//            String[] parts = dateStr.split("[/-]");
//            if (parts.length == 3) {
//                int day = Integer.parseInt(parts[0]);
//                int month = Integer.parseInt(parts[1]);
//                int year = Integer.parseInt(parts[2]);
//
//                // Ensure day and month are in correct order
//                if (month > 12) {
//                    int temp = day;
//                    day = month;
//                    month = temp;
//                }
//
//                return String.format("%d/%d/%d", month, day, year);
//            }
//        }
//        return dateStr;
//    }
    // new parsing code ended here

    //new modified code for parsing
    public static LocalDate parseDate(String dateStr) {
        if ("Present".equalsIgnoreCase(dateStr) || "Till Date".equalsIgnoreCase(dateStr) || "Current".equalsIgnoreCase(dateStr) || "CURRENT".equalsIgnoreCase(dateStr)) {
            return LocalDate.now();
        }

        DateTimeFormatter primaryFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter[] alternativeFormatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("M-d-yyyy"),
                DateTimeFormatter.ofPattern("M/d/yy"),
                DateTimeFormatter.ofPattern("M-d-yy"),
                DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM-yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("M/yyyy"),
                DateTimeFormatter.ofPattern("M-yyyy"),
                DateTimeFormatter.ofPattern("MMM/yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")  // Added formatter for full date
        };

        // Preprocess to ensure correct day and month order
        String preprocessedDateStr = preprocessDate(dateStr);

        // Handle specific yyyy-MM or yyyy/MM formats directly
        if (preprocessedDateStr.matches("\\d{4}-\\d{2}") || preprocessedDateStr.matches("\\d{4}/\\d{2}")) {
            // Parse as yyyy-MM or yyyy/MM directly with appended day
            return LocalDate.parse(preprocessedDateStr + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        try {
            return LocalDate.parse(preprocessedDateStr, primaryFormatter);
        } catch (DateTimeParseException e) {
            for (DateTimeFormatter formatter : alternativeFormatters) {
                try {
                    return LocalDate.parse(preprocessedDateStr, formatter);
                } catch (DateTimeParseException ignored) {
                }
            }
        }

        try {
            String[] parts = dateStr.split("[\\s/-]");
            int month = MONTH_MAP.getOrDefault(parts[0].toLowerCase(), 1);
            int year = Integer.parseInt(parts[parts.length - 1]);
            return LocalDate.of(year, month, 1);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse date: " + dateStr, e);
        }
    }

    private static String preprocessDate(String dateStr) {
        if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}") || dateStr.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
            String[] parts = dateStr.split("[/-]");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                // Ensure day and month are in correct order
                if (month > 12) {
                    int temp = day;
                    day = month;
                    month = temp;
                }

                return String.format("%d/%d/%d", month, day, year);
            }
        }
        return dateStr;
    }


    public static int calculateDurationInMonths(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }










}

