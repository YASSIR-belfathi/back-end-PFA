package rag_system.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rag_system.demo.services.ChatAiService;

@RestController
@RequestMapping("/chat")
public class ChatRestController {
    private final ChatAiService chatAiService;

    public ChatRestController(ChatAiService chatAiService) {
        this.chatAiService = chatAiService;
    }

    @GetMapping("/ask")
    public String ask(String question) {
        // Changer ragChatClient en ragChatClientWithAgent
        return chatAiService.ragChatClientWithAgent(question);
    }
}