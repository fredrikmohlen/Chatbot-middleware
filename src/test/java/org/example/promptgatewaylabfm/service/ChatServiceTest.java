package org.example.promptgatewaylabfm.service;

import org.example.promptgatewaylabfm.controller.ChatRequest;
import org.example.promptgatewaylabfm.controller.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private MemoryService memoryService;

    @Mock
    private PersonalityService personalityService;

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(memoryService, personalityService, restClient);
    }

    @Test
    void chat_shouldReturnAiResponseAndSaveToMemory() {
        ChatRequest request = new ChatRequest("pirate", "Hello", "session-123");

        when(personalityService.getSystemPrompt("pirate")).thenReturn("You are a pirate");

        when(memoryService.getHistory("session-123")).thenReturn(List.of());

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(ChatService.OpenRouterRequest.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        ChatService.ExternalResponse fakeResponse = new ChatService.ExternalResponse(
                List.of(new ChatService.ExternalResponse.Choice(new ChatMessage("assistant", "Ahoy!")))
        );
        when(responseSpec.body(ChatService.ExternalResponse.class)).thenReturn(fakeResponse);

        ChatResponse result = chatService.chat(request);

        assertEquals("Ahoy!", result.reply());

        verify(memoryService).addUserMessage("session-123", "Hello");
        verify(memoryService).addAssistantMessage("session-123", "Ahoy!");
    }

    @Test
    void fallback_shouldReturnStaticErrorMessage() {
        ChatRequest request = new ChatRequest("pirate", "Hello", "session-1");
                Exception fakeException = new RuntimeException("Connection failed");

        ChatResponse result = chatService.fallback(request, fakeException);

        String expectedMessage = "The AI service is temporarily unavailable. Please try again.";
        assertEquals(expectedMessage, result.reply(), "Fallback-meddelandet matchar inte det förväntade.");
    }
}
