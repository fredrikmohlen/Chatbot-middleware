package org.example.promptgatewaylabfm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PersonalityServiceTest {

    private PersonalityService personalityService;

    @BeforeEach
    void setUp() {
        personalityService = new PersonalityService();
    }

    @Test
    void getSystemPrompt_shouldReturnPiratePrompt() {
        // ACT
        String result = personalityService.getSystemPrompt("pirate");

        // ASSERT
        assertTrue(result.contains("Arrr!"), "Pirate prompt should contain pirate-slang");
    }

    @Test
    void getSystemPrompt_shouldReturnCoderPrompt() {
        // ACT
        String result = personalityService.getSystemPrompt("coder");

        // ASSERT
        assertTrue(result.contains("senior software engineer"), "Coder prompt should mention engineer");
    }

    @Test
    void getSystemPrompt_shouldReturnDefaultPrompt_whenUnknownPersonality() {
        // ACT
        String result = personalityService.getSystemPrompt("ninja");

        // ASSERT
        assertTrue(result.contains("friendly assistant"), "Should return default prompt for unknown personalities");
    }

    @ParameterizedTest
    @ValueSource(strings = {"PIRATE", "Pirate", "piRaTe"})
    void getSystemPrompt_shouldBeCaseInsensitive(String input) {
        // ACT
        String result = personalityService.getSystemPrompt(input);

        // ASSERT
        assertTrue(result.contains("Arrr!"), "Should handle different casing for: " + input);
    }
}
