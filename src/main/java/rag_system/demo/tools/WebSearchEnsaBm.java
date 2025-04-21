package rag_system.demo.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Function;

@Service("webSearchEnsaBm")
@Description("""
        Effectue une recherche sur le site web de l'ENSA Beni Mellal pour obtenir des informations
        lorsqu'elles ne sont pas disponibles dans la base de connaissances.
        """)
public class WebSearchEnsaBm
        implements Function<WebSearchEnsaBm.Request, WebSearchEnsaBm.Response> {

    public record Response(
            String information,
            String sourceUrl
    ){};

    public record Request(
            String query,
            String category  // Par exemple: "formation", "admission", "recherche", etc.
    ){};

    @Override
    public Response apply(Request request) {
        try {
            // URL de base du site
            String baseUrl = "https://ensabm.usms.ac.ma/";

            // Déterminer l'URL spécifique en fonction de la catégorie
            String searchUrl = switch (request.category().toLowerCase()) {
                case "formation" -> baseUrl + "formation/";
                case "admission" -> baseUrl + "admission/";
                case "recherche" -> baseUrl + "recherche/";
                // Ajoutez d'autres catégories au besoin
                default -> baseUrl;
            };

            // Connexion au site et récupération du contenu
            Document doc = Jsoup.connect(searchUrl).get();

            // Extraction d'informations pertinentes (à personnaliser selon la structure du site)
            String extractedInfo = "";

            // Exemple de recherche simple basée sur les mots-clés de la requête
            String[] keywords = request.query().toLowerCase().split("\\s+");

            // Recherche dans le contenu de la page
            String pageText = doc.text().toLowerCase();
            boolean containsKeywords = true;

            for (String keyword : keywords) {
                if (!pageText.contains(keyword)) {
                    containsKeywords = false;
                    break;
                }
            }

            if (containsKeywords) {
                // Trouver des paragraphes pertinents (à améliorer selon la structure du site)
                extractedInfo = doc.select("p, h1, h2, h3, li")
                        .stream()
                        .filter(element -> {
                            String text = element.text().toLowerCase();
                            boolean relevant = true;
                            for (String keyword : keywords) {
                                if (!text.contains(keyword)) {
                                    relevant = false;
                                    break;
                                }
                            }
                            return relevant;
                        })
                        .map(element -> element.text())
                        .limit(3) // Limiter à 3 extraits
                        .reduce("", (a, b) -> a + "\n\n" + b);
            }

            if (extractedInfo.isEmpty()) {
                return new Response(
                        "Aucune information pertinente trouvée sur le site pour cette requête.",
                        searchUrl
                );
            } else {
                return new Response(extractedInfo.trim(), searchUrl);
            }

        } catch (IOException e) {
            return new Response(
                    "Impossible d'accéder au site de l'ENSA Beni Mellal. Erreur: " + e.getMessage(),
                    "https://ensabm.usms.ac.ma/"
            );
        }
    }
}