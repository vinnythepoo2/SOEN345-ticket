package com.example.soen345_ticket.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class FilterHelperTest {

    // ── getFilterHint ─────────────────────────────────────────────────────────

    @Test
    public void getFilterHint_datePosition_returnsDateHint() {
        assertEquals("e.g. 2024-01-15", FilterHelper.getFilterHint(0));
    }

    @Test
    public void getFilterHint_categoryPosition_returnsCategoryHint() {
        assertEquals("e.g. Music, Sports, Tech", FilterHelper.getFilterHint(1));
    }

    @Test
    public void getFilterHint_locationPosition_returnsLocationHint() {
        assertEquals("e.g. Montreal, Quebec", FilterHelper.getFilterHint(2));
    }

    @Test
    public void getFilterHint_negativePosition_returnsEmpty() {
        assertEquals("", FilterHelper.getFilterHint(-1));
    }

    @Test
    public void getFilterHint_outOfBoundsPosition_returnsEmpty() {
        assertEquals("", FilterHelper.getFilterHint(FilterHelper.FILTER_HINTS.length));
    }

    @Test
    public void filterHints_arrayHasThreeEntries() {
        assertEquals(3, FilterHelper.FILTER_HINTS.length);
    }

    // ── toFirebaseField ───────────────────────────────────────────────────────

    @Test
    public void toFirebaseField_date_returnsLowercase() {
        assertEquals("date", FilterHelper.toFirebaseField("Date"));
    }

    @Test
    public void toFirebaseField_category_returnsLowercase() {
        assertEquals("category", FilterHelper.toFirebaseField("Category"));
    }

    @Test
    public void toFirebaseField_location_returnsLowercase() {
        assertEquals("location", FilterHelper.toFirebaseField("Location"));
    }

    @Test
    public void toFirebaseField_alreadyLowercase_unchanged() {
        assertEquals("date", FilterHelper.toFirebaseField("date"));
    }

    @Test
    public void toFirebaseField_null_returnsEmpty() {
        assertEquals("", FilterHelper.toFirebaseField(null));
    }

    // ── isValidInput ──────────────────────────────────────────────────────────

    @Test
    public void isValidInput_normalText_returnsTrue() {
        assertTrue(FilterHelper.isValidInput("Montreal"));
    }

    @Test
    public void isValidInput_emptyString_returnsFalse() {
        assertFalse(FilterHelper.isValidInput(""));
    }

    @Test
    public void isValidInput_whitespaceOnly_returnsFalse() {
        assertFalse(FilterHelper.isValidInput("   "));
    }

    @Test
    public void isValidInput_null_returnsFalse() {
        assertFalse(FilterHelper.isValidInput(null));
    }

    // ── buildRangeEnd ─────────────────────────────────────────────────────────

    @Test
    public void buildRangeEnd_normalPrefix_appendsSentinel() {
        assertEquals("Concert\uf8ff", FilterHelper.buildRangeEnd("Concert"));
    }

    @Test
    public void buildRangeEnd_emptyPrefix_returnsSentinelOnly() {
        assertEquals("\uf8ff", FilterHelper.buildRangeEnd(""));
    }

    @Test
    public void buildRangeEnd_null_returnsSentinelOnly() {
        assertEquals("\uf8ff", FilterHelper.buildRangeEnd(null));
    }

    @Test
    public void buildRangeEnd_endsWith_uf8ff() {
        String result = FilterHelper.buildRangeEnd("abc");
        assertTrue(result.endsWith("\uf8ff"));
    }
}
