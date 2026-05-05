package org.example.promptgatewaylabfm.service;

import org.example.promptgatewaylabfm.controller.ChatRequest;
import org.example.promptgatewaylabfm.controller.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ChatService {

    private final MemoryService memoryService;
    private final PersonalityService personalityService;
    private final RestClient restClient;

    public ChatService(MemoryService memoryService, PersonalityService personalityService, RestClient restClient) {
        this.memoryService = memoryService;
        this.personalityService = personalityService;
        this.restClient = restClient;
    }

    public ChatResponse chat(ChatRequest chatRequest) {

        String systemPrompt = personalityService.setPersonality(chatRequest.personality());

        var history = memoryService.getHistory(chatRequest.sessionId());

        String userMessage = chatRequest.message();

        ChatPayload chatPayload = new ChatPayload(systemPrompt, history, userMessage);

        return null;
    }
    public record ChatPayload(
            String systemPrompt,
            List<ChatMessage> history,
            String userMessage
    ){}
//    hämta system prompt baserat på personality
//    hämta historik från MemoryService
//    bygga payload till modellen
//    skicka HTTP‑request till OpenRouter/LM Studio
//    ta emot deras HTTP‑response
//    spara historik
//    skapa ChatResponse
}
