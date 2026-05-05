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

        String systemPrompt = personalityService.getSystemPrompt(chatRequest.personality());

        var history = memoryService.getHistory(chatRequest.sessionId());

        String userMessage = chatRequest.message();

        ChatPayload payload = new ChatPayload(systemPrompt, history, userMessage);

        ExternalResponse externalResponse = restClient.post()
                .uri("http://localhost:1234/v1/chat/completions")
                .body(payload)
                .retrieve()
                .body(ExternalResponse.class);

        memoryService.addUserMessage(chatRequest.sessionId(), userMessage);

        memoryService.addAssistantMessage(chatRequest.sessionId(), externalResponse.reply());

        return new ChatResponse(externalResponse.reply());
    }
    public record ChatPayload(
            String systemPrompt,
            List<ChatMessage> history,
            String userMessage
    ){}
    public record ExternalResponse(String reply){}
}
