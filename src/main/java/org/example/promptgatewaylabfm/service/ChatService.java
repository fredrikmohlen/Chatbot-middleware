package org.example.promptgatewaylabfm.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.example.promptgatewaylabfm.controller.ChatRequest;
import org.example.promptgatewaylabfm.controller.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
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
    @CircuitBreaker(name = "chatService", fallbackMethod = "fallback")
    @Retry(name = "chatService")
    public ChatResponse chat(ChatRequest chatRequest) {

        String systemPrompt = personalityService.getSystemPrompt(chatRequest.personality());
        List<ChatMessage> fullMessages = new ArrayList<>();

        fullMessages.add(new ChatMessage("system", systemPrompt));

        fullMessages.addAll(memoryService.getHistory(chatRequest.sessionId()));

        fullMessages.add(new ChatMessage("user", chatRequest.message()));

        ExternalResponse response = restClient.post()
                .uri("/chat/completions")
                .body(new OpenRouterRequest("openrouter/owl-alpha", fullMessages))
                .retrieve()
                .onStatus(s -> s.value() == 429 || s.value() == 500,
                        (req, resp) -> {
                    throw new RetryableHttpException("AI service not responding, trying again");
                        })
                .body(ExternalResponse.class);

                //free AI-bots:openrouter/free

        String aiReply = (response != null && !response.choices().isEmpty())
                ? response.choices().getFirst().message().content()
                : "I didn't get an answer from the AI";

        memoryService.addUserMessage(chatRequest.sessionId(), chatRequest.message());
        memoryService.addAssistantMessage(chatRequest.sessionId(),  aiReply);

        return new ChatResponse(aiReply);
    }
    // Optional: What to do if all retries fail
    public String fallback(Exception e) {
        return "Fallback!" + e.getMessage();
    }

    public record OpenRouterRequest(
            String model,
            List<ChatMessage> messages
    ){}
    public record ExternalResponse(List<Choice> choices){
        public record Choice(ChatMessage message){}
    }
}
