package com.vectorio.vector.store;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentRepository {
    private final VectorStore vectorStore;
    private boolean flag = false;

    public DocumentRepository(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    private final List<Document> documents = List.of(
            new Document("Фамилия Болата Мусылманбеков"),
            new Document("Дата рождения Болата Мусылманбекова 21 января 1999 года")
    );
    public void addTestDocuments(){
        vectorStore.add(documents);
        flag = true;
    }

    public List<Document> result(String request) throws InterruptedException {
        if (flag){
            return vectorStore.similaritySearch(SearchRequest.builder().query(request).topK(5).build());
        }
        addTestDocuments();
        Thread.sleep(3000);
        return vectorStore.similaritySearch(SearchRequest.builder().query(request).topK(5).build());
    }

}
