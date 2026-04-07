package com.example.soen345_ticket.services;

import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EmailService {

    private static final String API_URL  = "https://api.emailjs.com/api/v1.0/email/send";

    private static final String SERVICE_ID  = "service_of4etu4";
    private static final String TEMPLATE_ID = "template_4vsvzpx";
    private static final String PUBLIC_KEY  = "9cijQnqUbAi1q5I9J";

    private final Executor   executor;
    private final HttpSender httpSender;

    interface ConnectionFactory {
        HttpURLConnection open(URL url) throws IOException;
    }

    /** Production constructor — uses real HTTP. */
    public EmailService() {
        this(Executors.newSingleThreadExecutor(), buildRealSender());
    }

    /** Package-private constructor for tests — caller supplies a stub HttpSender. */
    EmailService(Executor executor, HttpSender httpSender) {
        this.executor   = executor;
        this.httpSender = httpSender;
    }

    public interface EmailCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void sendBookingConfirmation(
            String toEmail,
            String eventTitle,
            String eventDate,
            String eventLocation,
            int quantity,
            double totalPrice,
            String bookingDate,
            EmailCallback callback) {

        executor.execute(() -> {
            try {
                String body = EmailRequestBuilder.build(
                        SERVICE_ID, TEMPLATE_ID, PUBLIC_KEY,
                        toEmail, eventTitle, eventDate, eventLocation,
                        quantity, totalPrice, bookingDate);

                int responseCode = httpSender.send(body);

                if (responseCode == 200) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("HTTP " + responseCode);
                }
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    private static HttpSender buildRealSender() {
        return buildRealSender(url -> (HttpURLConnection) url.openConnection());
    }

    static HttpSender buildRealSender(ConnectionFactory connectionFactory) {
        return jsonBody -> {
            URL url = new URL(API_URL);
            HttpURLConnection conn = connectionFactory.open(url);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("origin", "http://localhost");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
            int code = conn.getResponseCode();
            conn.disconnect();
            return code;
        };
    }
}
