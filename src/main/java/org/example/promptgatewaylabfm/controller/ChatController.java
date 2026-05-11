package org.example.promptgatewaylabfm.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.promptgatewaylabfm.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send a chat message", description = "Sends a message to the AI...")
    public ChatResponse chat(@RequestBody ChatRequest request){
        return chatService.chat(request);
    }
}
