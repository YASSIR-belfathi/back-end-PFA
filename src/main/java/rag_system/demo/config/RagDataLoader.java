package rag_system.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
public class RagDataLoader {

    private String pdfPath = "C:\\Users\\user\\Desktop\\projets_S3_TDI\\back-end-PFA\\src\\main\\resources\\docs\\TSP_presentation.pdf";
    @Value("classpath:/docs/TSP_presentation.pdf")
    private Resource pdfResource;
    @Value("classpath:/docs/TSP_presentation.pdf")
    private Resource doc001;
    @Value("store-data-v1.json")
    private String storedoc;
    private JdbcClient jdbcClient;
    private VectorStore vectorStore;
    public RagDataLoader(JdbcClient jdbcClient, VectorStore vectorStore) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
    }

    //@Bean
//    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
//        SimpleVectorStore vectorStore;
//        vectorStore = SimpleVectorStore.builder(embeddingModel).build();
//        String docStore = Path.of("src","main","resources","store")
//                        .toAbsolutePath()+"/"+storedoc;
//
//        File file = new File(docStore);
//
//        if (!file.exists()) {
//            try {
//                // Use Java's File directly
//                File pdfFile = new File(pdfPath);
//
//                if (!pdfFile.exists()) {
//                    System.err.println("PDF file doesn't exist at: " + pdfPath);
//                    throw new RuntimeException("PDF file not found at: " + pdfPath);
//                }
//
//                System.out.println("PDF file found: " + pdfFile.getAbsolutePath());
//
//                // IMPORTANT CHANGE: Create a FileSystemResource instead
//                org.springframework.core.io.FileSystemResource fileResource =
//                        new org.springframework.core.io.FileSystemResource(pdfFile);
//
//                // Create PDF reader with FileSystemResource
//                PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(fileResource);
//                List<Document> documents = pagePdfDocumentReader.get();
//
//                System.out.println("Successfully read " + documents.size() + " pages from PDF");
//
//                TextSplitter textSplitter = new TokenTextSplitter();
//                List<Document> chunks = textSplitter.split(documents);
//
//                System.out.println("Split into " + chunks.size() + " chunks");
//
//                vectorStore.accept(chunks);
//
//                // Ensure directory exists
//                File storeDir = file.getParentFile();
//                if (!storeDir.exists()) {
//                    storeDir.mkdirs();
//                    System.out.println("Created directory: " + storeDir.getAbsolutePath());
//                }
//
//                vectorStore.save(file);
//                System.out.println("Vector store saved to: " + file.getAbsolutePath());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("Error processing PDF: " + e.getMessage(), e);
//            }
//        } else {
//            System.out.println("Loading existing vector store from: " + file.getAbsolutePath());
//            vectorStore.load(file);
//        }
//        return vectorStore;
//
//    }


    @PostConstruct
    public void initStore() {
        try {
            boolean isEmpty = jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                    .query(Integer.class)
                    .optional()
                    .orElse(0) == 0;

            if (isEmpty) {
                loadPdfIntoVectorStore();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize vector store", e);
        }
    }

    private void loadPdfIntoVectorStore() throws IOException {
        File pdfFile = pdfResource.getFile();
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(new FileSystemResource(pdfFile));
        List<Document> documents = pdfReader.get();

        TextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.split(documents);

        vectorStore.accept(chunks);
        System.out.println("Loaded " + chunks.size() + " chunks into vector store.");
    }
}


