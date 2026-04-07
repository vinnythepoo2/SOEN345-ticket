package com.example.soen345_ticket.services;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EmailServiceTest {

    // Stubs that record the last body sent and return a fixed status code
    private static class FakeSender implements HttpSender {
        final int responseCode;
        String lastBody;

        FakeSender(int responseCode) { this.responseCode = responseCode; }

        @Override
        public int send(String jsonBody) {
            lastBody = jsonBody;
            return responseCode;
        }
    }

    private static class FailingSender implements HttpSender {
        @Override
        public int send(String jsonBody) throws IOException {
            throw new IOException("connection refused");
        }
    }

    // Helper: runs sendBookingConfirmation synchronously using the same-thread executor
    private static class Result {
        boolean success;
        String  error;
    }

    private Result call(HttpSender sender) throws InterruptedException {
        EmailService service = new EmailService(Executors.newSingleThreadExecutor(), sender);
        CountDownLatch latch = new CountDownLatch(1);
        Result result = new Result();

        service.sendBookingConfirmation(
                "user@example.com", "Jazz Night", "2024-06-15",
                "Montreal", 2, 100.0, "2024-01-10 09:00",
                new EmailService.EmailCallback() {
                    @Override public void onSuccess() {
                        result.success = true;
                        latch.countDown();
                    }
                    @Override public void onFailure(String error) {
                        result.error = error;
                        latch.countDown();
                    }
                });

        latch.await(3, TimeUnit.SECONDS);
        return result;
    }

    // ── Success path ──────────────────────────────────────────────────────────

    @Test
    public void send_http200_callsOnSuccess() throws Exception {
        Result r = call(new FakeSender(200));
        assertTrue(r.success);
        assertNull(r.error);
    }

    @Test
    public void send_http200_doesNotCallOnFailure() throws Exception {
        Result r = call(new FakeSender(200));
        assertNull(r.error);
    }

    // ── Failure paths ─────────────────────────────────────────────────────────

    @Test
    public void send_http400_callsOnFailureWithCode() throws Exception {
        Result r = call(new FakeSender(400));
        assertFalse(r.success);
        assertEquals("HTTP 400", r.error);
    }

    @Test
    public void send_http500_callsOnFailureWithCode() throws Exception {
        Result r = call(new FakeSender(500));
        assertEquals("HTTP 500", r.error);
    }

    @Test
    public void send_ioException_callsOnFailureWithMessage() throws Exception {
        Result r = call(new FailingSender());
        assertFalse(r.success);
        assertNotNull(r.error);
        assertTrue(r.error.contains("connection refused"));
    }

    // ── Request body is forwarded to the sender ───────────────────────────────

    @Test
    public void send_forwardsJsonBodyToSender() throws Exception {
        FakeSender sender = new FakeSender(200);
        call(sender);
        assertNotNull(sender.lastBody);
        assertTrue(sender.lastBody.contains("user@example.com"));
        assertTrue(sender.lastBody.contains("Jazz Night"));
    }

    // ── Default (production) constructor ──────────────────────────────────────

    @Test
    public void defaultConstructor_doesNotThrow() {
        // Exercises EmailService() and buildRealSender(); the lambda body is never
        // invoked so no real network call is made.
        new EmailService();
    }
}
