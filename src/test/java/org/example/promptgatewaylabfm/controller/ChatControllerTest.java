package org.example.promptgatewaylabfm.controller;

import org.example.promptgatewaylabfm.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void chat_shouldReturnOkAndResponse() throws Exception {

        ChatRequest request = new ChatRequest("pirate", "Hello", "session-1");
        ChatResponse expectedResponse = new ChatResponse("Ahoy there!");

        when(chatService.chat(any(ChatRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Ahoy there!"));
    }

    @Test
    void chat_shouldReturnBadRequest_whenPersonalityIsBlank() throws Exception {
        ChatRequest invalidRequest = new ChatRequest("", "Hello", "session-1");

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Vi förväntar oss 400 Bad Request
    }
}
