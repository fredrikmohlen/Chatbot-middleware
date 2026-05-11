package org.example.promptgatewaylabfm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryServiceTest {

    private MemoryService memoryService;

    @BeforeEach
    void setUp() {
        memoryService = new MemoryService();
    }

    @Test
    void getHistory_shouldReturnEmptyList_whenSessionDoesNotExist() {
        // ACT
        List<ChatMessage> history = memoryService.getHistory("non-existent");

        // ASSERT
        assertTrue(history.isEmpty(), "Historiken borde vara tom för en ny session");
    }

    @Test
    void addUserMessage_shouldAddMessageCorrectly() {
        // ARRANGE
        String sessionId = "session-1";
        String message = "Hello AI";

        // ACT
        memoryService.addUserMessage(sessionId, message);
        List<ChatMessage> history = memoryService.getHistory(sessionId);

        // ASSERT
        assertEquals(1, history.size());
        assertEquals("user", history.get(0).role());
        assertEquals(message, history.get(0).content());
    }

    @Test
    void addAssistantMessage_shouldAddMessageCorrectly() {
        // ARRANGE
        String sessionId = "session-1";
        String aiReply = "Hello Human";

        // ACT
        memoryService.addAssistantMessage(sessionId, aiReply);
        List<ChatMessage> history = memoryService.getHistory(sessionId);

        // ASSERT
        assertEquals(1, history.size());
        assertEquals("assistant", history.get(0).role());
        assertEquals(aiReply, history.get(0).content());
    }

    @Test
    void history_shouldMaintainOrderAndKeepSessionsApart() {
        // ARRANGE
        String session1 = "user-a";
        String session2 = "user-b";

        // ACT
        memoryService.addUserMessage(session1, "Msg 1 from A");
        memoryService.addAssistantMessage(session1, "Reply to A");

        memoryService.addUserMessage(session2, "Msg 1 from B");

        // ASSERT
        List<ChatMessage> historyA = memoryService.getHistory(session1);
        List<ChatMessage> historyB = memoryService.getHistory(session2);

        assertEquals(2, historyA.size());
        assertEquals("Msg 1 from A", historyA.get(0).content());
        assertEquals("assistant", historyA.get(1).role());

        assertEquals(1, historyB.size());
        assertEquals("Msg 1 from B", historyB.get(0).content());
    }

    @Test
    void getHistory_shouldReturnImmutableList() {
        // ARRANGE
        memoryService.addUserMessage("session-1", "Hello");
        List<ChatMessage> history = memoryService.getHistory("session-1");

        // ACT & ASSERT
        assertThrows(UnsupportedOperationException.class, () -> {
            history.add(new ChatMessage("user", "Try to hack history"));
        }, "Historiken som returneras ska vara skrivskyddad (immutable)");
    }
}


