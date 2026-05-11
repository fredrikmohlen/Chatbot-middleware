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
import org.mockito.ArgumentCaptor;

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
        String systemPrompt = "You are a pirate";
        ChatMessage historyMessage = new ChatMessage("assistant", "Old context");

        when(personalityService.getSystemPrompt("pirate")).thenReturn(systemPrompt);

        when(memoryService.getHistory("session-123")).thenReturn(List.of(historyMessage));

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);

        ArgumentCaptor<ChatService.OpenRouterRequest> requestCaptor =
                ArgumentCaptor.forClass(ChatService.OpenRouterRequest.class);

        when(requestBodySpec.body(requestCaptor.capture())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        ChatService.ExternalResponse fakeResponse = new ChatService.ExternalResponse(
                List.of(new ChatService.ExternalResponse.Choice(new ChatMessage("assistant", "Ahoy!")))
        );
        when(responseSpec.body(ChatService.ExternalResponse.class)).thenReturn(fakeResponse);

        // 2. ACT
        ChatResponse result = chatService.chat(request);

        // 3. ASSERT
        assertEquals("Ahoy!", result.reply());

        ChatService.OpenRouterRequest capturedPayload = requestCaptor.getValue();
        List<ChatMessage> messages = capturedPayload.messages();

        assertEquals(3, messages.size(), "Payload should contain system prompt, history and new message");
        assertEquals("system", messages.get(0).role());
        assertEquals(systemPrompt, messages.get(0).content());
        assertEquals("assistant", messages.get(1).role()); // Historiken
        assertEquals("user", messages.get(2).role());
        assertEquals("Hello", messages.get(2).content());

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
