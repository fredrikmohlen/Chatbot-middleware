package org.example.promptgatewaylabfm.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MemoryService {

    // Ska spara request och response per SessionsID
    private final Map<String, List<ChatMessage>> history = new ConcurrentHashMap<>();

    // Hämta hela historiken
    public List<ChatMessage> getHistory(String sessionId){
        return List.copyOf(history.getOrDefault(sessionId, List.of()));

    }
    // Lägg till ett meddelande från användaren
    public void addUserMessage(String sessionId, String message){
        history.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>())
                .add(new ChatMessage("user", message));
    }
    // Lägg till ett svar från AIn
    public void addAssistantMessage(String sessionId, String message){
        history.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>())
                .add(new ChatMessage("assistant", message));
    }
}
