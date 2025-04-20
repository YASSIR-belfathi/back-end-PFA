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
    private final SchoolAgentService schoolAgentService;

    public ChatAiService(ChatClient.Builder builder,
                         VectorStore vectorStore,
                         SchoolAgentService schoolAgentService) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.schoolAgentService = schoolAgentService;
    }

    public String ragChatClient(String question) {
        // 1. Recherche RAG
        List<Document> documents = vectorStore.similaritySearch(question);
        String context = "";

        if (!documents.isEmpty()) {
            context = documents.stream()
                    .map(Document::getFormattedContent)
                    .collect(Collectors.joining("\n"));
        } else {
            // 2. Si RAG vide, utiliser l'agent
            try {
                context = schoolAgentService.getInformation(question);
            } catch (Exception e) {
                return "Désolé, je n'ai pas trouvé d'informations pertinentes.";
            }
        }

        // 3. Génération de la réponse
        String systemMessage = """
                Répondez en français en utilisant uniquement le contexte fourni.
                Si l'information n'est pas dans le contexte, répondez : "Je ne dispose pas de ces informations."

                Contexte :
                {context}

                Question :
                {question}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(systemMessage);
        Prompt prompt = promptTemplate.create(
                Map.of("context", context, "question", question));

        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}