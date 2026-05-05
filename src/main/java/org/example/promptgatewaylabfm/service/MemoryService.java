package org.example.promptgatewaylabfm.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryService {

    // ska spara request och response per SessionsID
    private final Map<String, List<ChatMessage>> history = new ConcurrentHashMap<>();

    // hämta hela historiken
    public List<ChatMessage> getHistory(String sessionId){
        return history.getOrDefault(sessionId, new ArrayList<>());
    }
// Lägg till ett meddelande från användaren
    public void addUserMessage(String sessionId, String message){
        history.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ChatMessage("user", message));
    }
// Lägg till ett svar från AIn
    public void addAssistantMessage(String sessionId, String message){
        history.computeIfAbsent(sessionId, k -> new ArrayList<>())
                .add(new ChatMessage("assistant", message));
    }
}
