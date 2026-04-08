package com.example.soen345_ticket.services;

import static org.junit.Assert.*;
import org.json.JSONObject;
import org.junit.Test;

public class EmailRequestBuilderTest {

    // Reusable fixture values
    private static final String SERVICE_ID   = "svc_test";
    private static final String TEMPLATE_ID  = "tmpl_test";
    private static final String PUBLIC_KEY   = "pub_test";
    private static final String TO_EMAIL     = "user@example.com";
    private static final String EVENT_TITLE  = "Jazz Night";
    private static final String EVENT_DATE   = "2024-06-15";
    private static final String EVENT_LOC    = "Montreal";
    private static final int    QUANTITY     = 3;
    private static final double TOTAL_PRICE  = 150.0;
    private static final String BOOKING_DATE = "2024-01-10 09:00";

    private JSONObject build() throws Exception {
        String json = EmailRequestBuilder.build(
                SERVICE_ID, TEMPLATE_ID, PUBLIC_KEY,
                TO_EMAIL, EVENT_TITLE, EVENT_DATE, EVENT_LOC,
                QUANTITY, TOTAL_PRICE, BOOKING_DATE);
        return new JSONObject(json);
    }

    // ── Top-level API fields ──────────────────────────────────────────────────

    @Test
    public void build_containsServiceId() throws Exception {
        assertEquals(SERVICE_ID, build().getString("service_id"));
    }

    @Test
    public void build_containsTemplateId() throws Exception {
        assertEquals(TEMPLATE_ID, build().getString("template_id"));
    }

    @Test
    public void build_containsPublicKey() throws Exception {
        assertEquals(PUBLIC_KEY, build().getString("user_id"));
    }

    @Test
    public void build_containsTemplateParamsObject() throws Exception {
        assertTrue(build().has("template_params"));
    }

    // ── template_params fields ────────────────────────────────────────────────

    private JSONObject params() throws Exception {
        return build().getJSONObject("template_params");
    }

    @Test
    public void templateParams_containsToEmail() throws Exception {
        assertEquals(TO_EMAIL, params().getString("to_email"));
    }

    @Test
    public void templateParams_containsEventTitle() throws Exception {
        assertEquals(EVENT_TITLE, params().getString("event_title"));
    }

    @Test
    public void templateParams_containsEventDate() throws Exception {
        assertEquals(EVENT_DATE, params().getString("event_date"));
    }

    @Test
    public void templateParams_containsEventLocation() throws Exception {
        assertEquals(EVENT_LOC, params().getString("event_location"));
    }

    @Test
    public void templateParams_containsQuantity() throws Exception {
        assertEquals(QUANTITY, params().getInt("quantity"));
    }

    @Test
    public void templateParams_containsTotalPriceFormatted() throws Exception {
        assertEquals("150.00", params().getString("total_price"));
    }

    @Test
    public void templateParams_containsBookingDate() throws Exception {
        assertEquals(BOOKING_DATE, params().getString("booking_date"));
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test
    public void build_totalPrice_formattedToTwoDecimals() throws Exception {
        String json = EmailRequestBuilder.build(
                SERVICE_ID, TEMPLATE_ID, PUBLIC_KEY,
                TO_EMAIL, EVENT_TITLE, EVENT_DATE, EVENT_LOC,
                1, 9.5, BOOKING_DATE);
        String price = new JSONObject(json).getJSONObject("template_params").getString("total_price");
        assertEquals("9.50", price);
    }

    @Test
    public void build_quantityZero_isIncluded() throws Exception {
        String json = EmailRequestBuilder.build(
                SERVICE_ID, TEMPLATE_ID, PUBLIC_KEY,
                TO_EMAIL, EVENT_TITLE, EVENT_DATE, EVENT_LOC,
                0, 0.0, BOOKING_DATE);
        int qty = new JSONObject(json).getJSONObject("template_params").getInt("quantity");
        assertEquals(0, qty);
    }

    @Test
    public void build_producesValidJson() throws Exception {
        // If JSONObject can parse it back, the output is valid JSON
        String json = EmailRequestBuilder.build(
                SERVICE_ID, TEMPLATE_ID, PUBLIC_KEY,
                TO_EMAIL, EVENT_TITLE, EVENT_DATE, EVENT_LOC,
                QUANTITY, TOTAL_PRICE, BOOKING_DATE);
        assertNotNull(new JSONObject(json));
    }
}
