package rag_system.demo.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatAiService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatAiService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public String ragChatClient(String question) {
        // Step 1: Retrieve relevant documents
        List<Document> documents = vectorStore.similaritySearch(question);
        if (documents.isEmpty()) {
            return "AT AWA OR ssingh Myd tennit"; // Fallback if no context is found
        }

        // Step 2: Extract context
        String context = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        // Step 3: Create a strict prompt
        String systemMessage = """
                Answer the question **only** using the provided context.
                If the answer is not in the context, respond with: "AT AWA OR ssingh Myd tennit".

                Context:
                {context}

                Question:
                {question}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(systemMessage);
        Prompt prompt = promptTemplate.create(
                Map.of("context", context, "question", question));

        // Step 4: Get the response
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}
