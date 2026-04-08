package com.example.soen345_ticket.services;

import org.json.JSONException;
import org.json.JSONObject;

/** Builds the JSON request body sent to the EmailJS REST API. */
public class EmailRequestBuilder {

    /**
     * Assembles the full EmailJS API payload.
     *
     * @return JSON string ready to be written to the HTTP request body.
     */
    public static String build(
            String serviceId,
            String templateId,
            String publicKey,
            String toEmail,
            String eventTitle,
            String eventDate,
            String eventLocation,
            int quantity,
            double totalPrice,
            String bookingDate) throws JSONException {

        JSONObject templateParams = new JSONObject();
        templateParams.put("to_email",       toEmail);
        templateParams.put("event_title",    eventTitle);
        templateParams.put("event_date",     eventDate);
        templateParams.put("event_location", eventLocation);
        templateParams.put("quantity",       quantity);
        templateParams.put("total_price",    String.format("%.2f", totalPrice));
        templateParams.put("booking_date",   bookingDate);

        JSONObject body = new JSONObject();
        body.put("service_id",      serviceId);
        body.put("template_id",     templateId);
        body.put("user_id",         publicKey);
        body.put("template_params", templateParams);

        return body.toString();
    }

    private EmailRequestBuilder() {}
}
