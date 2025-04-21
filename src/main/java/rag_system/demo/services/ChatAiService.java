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
                2. Si l'information n'est pas dans le contexte RAG, utilisez la fonction 'webSearchEnsaBm'
                   pour rechercher directement sur le site web de l'école.
                3. Si les deux méthodes échouent, utilisez 'schoolIdentityInfo' pour obtenir au moins
                   des informations de base sur l'école.
                4. En dernier recours, indiquez: "Je ne dispose pas de ces informations."
                
                Pour la fonction webSearchEnsaBm, déterminez la catégorie la plus appropriée parmi:
                'formation', 'admission', 'recherche', ou laissez vide pour la page d'accueil.
                
                Contexte RAG:
                %s
                """.formatted(context);

        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(question));

        // 4. Création de la prompt et appel avec les fonctions disponibles
        Prompt prompt = new Prompt(messages);

        return chatClient.prompt(prompt)
                // Les deux outils sont disponibles, l'agent choisira le plus approprié
                .functions("webSearchEnsaBm", "schoolIdentityInfo")
                .call()
                .content();
    }
}