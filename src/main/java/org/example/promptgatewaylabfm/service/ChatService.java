package org.example.promptgatewaylabfm.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.example.promptgatewaylabfm.controller.ChatRequest;
import org.example.promptgatewaylabfm.controller.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    @CircuitBreaker(name = "chatService", fallbackMethod = "fallback")
    @Retry(name = "chatService")
    public ChatResponse chat(ChatRequest chatRequest) {
        if (chatRequest == null
                || chatRequest.sessionId() == null || chatRequest.sessionId().isBlank()
                || chatRequest.message() == null || chatRequest.message().isBlank()
                || chatRequest.personality() == null || chatRequest.personality().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "sessionId, personality and message are required");
        }
        String systemPrompt = personalityService.getSystemPrompt(chatRequest.personality());
        List<ChatMessage> fullMessages = new ArrayList<>();

        fullMessages.add(new ChatMessage("system", systemPrompt));

        fullMessages.addAll(memoryService.getHistory(chatRequest.sessionId()));

        fullMessages.add(new ChatMessage("user", chatRequest.message()));

        ExternalResponse response = restClient.post()
                .uri("/chat/completions")
                // Ai - model name from open router
                .body(new OpenRouterRequest("openrouter/owl-alpha", fullMessages))
                .retrieve()
                .onStatus(s -> s.value() == 429 || s.is5xxServerError(),
                        (req, resp) -> {
                            throw new RetryableHttpException("AI service not responding, trying again");
                        })
                .body(ExternalResponse.class);

        //free AI-bots:openrouter/free

        String aiReply = (response != null && !response.choices().isEmpty())
                ? response.choices().getFirst().message().content()
                : "I didn't get an answer from the AI";

        memoryService.addUserMessage(chatRequest.sessionId(), chatRequest.message());
        memoryService.addAssistantMessage(chatRequest.sessionId(), aiReply);

        return new ChatResponse(aiReply);
    }

    // Optional: What to do if all retries fail
    public ChatResponse fallback(ChatRequest request, Exception e) {
        return new ChatResponse("The AI service is temporarily unavailable. Please try again.");
    }

    public record OpenRouterRequest(
            String model,
            List<ChatMessage> messages
    ) {
    }

    public record ExternalResponse(List<Choice> choices) {
        public record Choice(ChatMessage message) {
        }
    }
}
