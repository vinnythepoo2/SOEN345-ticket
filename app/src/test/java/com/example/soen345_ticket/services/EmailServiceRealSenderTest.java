package com.example.soen345_ticket.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class EmailServiceRealSenderTest {

    private static class FakeHttpURLConnection extends HttpURLConnection {
        private final ByteArrayOutputStream body = new ByteArrayOutputStream();
        private int responseCode = 202;
        private boolean disconnected;

        protected FakeHttpURLConnection(URL u) {
            super(u);
        }

        @Override
        public void disconnect() {
            disconnected = true;
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {}

        @Override
        public OutputStream getOutputStream() {
            return body;
        }

        @Override
        public int getResponseCode() {
            return responseCode;
        }

        String getSentBody() {
            return body.toString(StandardCharsets.UTF_8);
        }
    }

    @Test
    public void buildRealSender_writesBodyAndReturnsResponseCode() throws IOException {
        final FakeHttpURLConnection[] connectionHolder = new FakeHttpURLConnection[1];
        HttpSender sender = EmailService.buildRealSender(url -> {
            FakeHttpURLConnection connection = new FakeHttpURLConnection(url);
            connectionHolder[0] = connection;
            return connection;
        });

        int result = sender.send("{\"hello\":\"world\"}");

        assertEquals(202, result);
        assertEquals("POST", connectionHolder[0].getRequestMethod());
        assertEquals("application/json", connectionHolder[0].getRequestProperty("Content-Type"));
        assertEquals("http://localhost", connectionHolder[0].getRequestProperty("origin"));
        assertTrue(connectionHolder[0].getDoOutput());
        assertEquals("{\"hello\":\"world\"}", connectionHolder[0].getSentBody());
        assertTrue(connectionHolder[0].disconnected);
    }
}
