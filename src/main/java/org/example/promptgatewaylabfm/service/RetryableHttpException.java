package org.example.promptgatewaylabfm.service;

public class RetryableHttpException extends RuntimeException {
    public RetryableHttpException(String message) {
        super(message);
    }
}
