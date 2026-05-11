package org.example.promptgatewaylabfm.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 50) String personality,
        @NotBlank @Size(max = 4000) String message,
        @NotBlank @Size(max = 128) String sessionId
) { }
