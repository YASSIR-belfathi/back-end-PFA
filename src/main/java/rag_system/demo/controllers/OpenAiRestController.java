package rag_system.demo.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OpenAiRestController {

    private final ChatClient chatClient;

    public OpenAiRestController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/hello")
    @PreAuthorize("hasRole('client_admin')")
    public String hello() {
        return "hello guys";
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String query) {
        SystemMessage systemMessage = new SystemMessage("""
                    give the answer of my prompt in structured way with paragraph, and each pragraph should have his
                    own title!
                """);
        UserMessage userMessage = new UserMessage(query);
        // String response = chatClient.prompt().user(query).call().content();
        Prompt prompt = new Prompt(systemMessage, userMessage);
        String response = chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
        return response;
    }
}
