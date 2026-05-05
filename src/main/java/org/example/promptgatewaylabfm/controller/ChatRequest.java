package org.example.promptgatewaylabfm.controller;

public record ChatRequest(
        String personality,
        String message,
        String sessionId
) { }
