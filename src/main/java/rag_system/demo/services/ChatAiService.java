package rag_system.demo.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatAiService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatAiService(ChatClient.Builder builder,
                         VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public String ragChatClientWithAgent(String question) {
        // 1. Recherche RAG
        List<Document> documents = vectorStore.similaritySearch(question);

        // 2. Construction du contexte
        String context = "";
        if (!documents.isEmpty()) {
            context = documents.stream()
                    .map(Document::getFormattedContent)
                    .collect(Collectors.joining("\n"));
        }

        // 3. Construction des messages pour l'agent
        List<Message> messages = new ArrayList<>();

        // Message système pour guider l'agent
        String systemPrompt = """
                Vous êtes un assistant spécialisé sur l'ENSA de Beni Mellal.
                Répondez en français en suivant ces règles prioritaires:

                1. Si le contexte RAG contient l'information, utilisez-la pour répondre.
                2. Si l'information n'est pas dans le contexte RAG et si le modèle le permet, utilisez la fonction 'webSearchEnsaBm'
                   pour rechercher directement sur le site web de l'école.
                3. Si les méthodes précédentes échouent et si le modèle le permet, utilisez 'schoolIdentityInfo' pour obtenir au moins
                   des informations de base sur l'école.
                4. En dernier recours, indiquez: "Je ne dispose pas de ces informations."

                Note: Certains modèles comme gemma:2b ne supportent pas les fonctions externes. Dans ce cas,
                utilisez uniquement les informations du contexte RAG ou vos connaissances générales.

                Pour la fonction webSearchEnsaBm, déterminez la catégorie la plus appropriée parmi:
                'formation', 'admission', 'recherche', ou laissez vide pour la page d'accueil.

                Contexte RAG:
                %s
                """.formatted(context);

        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(question));

        // 4. Création de la prompt et appel avec les fonctions disponibles
        Prompt prompt = new Prompt(messages);

        try {
            // Try to use functions if the model supports them
            return chatClient.prompt(prompt)
                    .functions("webSearchEnsaBm", "schoolIdentityInfo")
                    .call()
                    .content();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("does not support tools")) {
                // If the model doesn't support tools, fall back to basic prompt
                System.out.println("Model doesn't support tools, falling back to basic prompt");
                return chatClient.prompt(prompt)
                        .call()
                        .content();
            } else {
                // Re-throw other exceptions
                throw e;
            }
        }
    }
}
