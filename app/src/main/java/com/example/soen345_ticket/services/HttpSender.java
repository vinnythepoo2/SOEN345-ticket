package com.example.soen345_ticket.services;

import java.io.IOException;

/** Abstraction over the HTTP layer so EmailService can be tested without real network calls. */
public interface HttpSender {
    /** Sends {@code jsonBody} to the EmailJS endpoint and returns the HTTP response code. */
    int send(String jsonBody) throws IOException;
}
