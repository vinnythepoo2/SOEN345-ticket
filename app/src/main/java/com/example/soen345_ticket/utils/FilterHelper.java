package com.example.soen345_ticket.utils;

/**
 * Pure-logic helpers for the search/filter feature in MainActivity.
 * Kept Android-free so they can be covered by plain JUnit tests.
 */
public class FilterHelper {

    /** Hint strings parallel to the filter_options spinner array (Date, Category, Location). */
    public static final String[] FILTER_HINTS = {
        "e.g. 2024-01-15",
        "e.g. Music, Sports, Tech",
        "e.g. Montreal, Quebec"
    };

    /** Returns the hint string for the given spinner position, or "" for an out-of-range index. */
    public static String getFilterHint(int position) {
        if (position < 0 || position >= FILTER_HINTS.length) return "";
        return FILTER_HINTS[position];
    }

    /**
     * Returns the Firebase child field name for the given spinner selection label.
     * The label comes directly from the filter_options array (Date, Category, Location).
     */
    public static String toFirebaseField(String spinnerLabel) {
        if (spinnerLabel == null) return "";
        return spinnerLabel.toLowerCase();
    }

    /**
     * Returns true when the input string is non-null and non-blank,
     * i.e. a search/filter query should actually be executed.
     */
    public static boolean isValidInput(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Builds the Firebase range end value for a prefix query (startAt / endAt pattern).
     * Appends the Unicode private-use sentinel so the query matches all strings with
     * the given prefix.
     */
    public static String buildRangeEnd(String prefix) {
        if (prefix == null) return "\uf8ff";
        return prefix + "\uf8ff";
    }

    private FilterHelper() {}
}
