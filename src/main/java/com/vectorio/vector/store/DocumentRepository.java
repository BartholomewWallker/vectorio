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

    public void addTestDocuments(List<Document> documents){
        vectorStore.add(documents);
        flag = true;
    }

    public List<Document> result(String request) throws InterruptedException {
        if (flag){
            return vectorStore.similaritySearch(SearchRequest.builder().query(request).topK(5).build());
        }
        Thread.sleep(3000);
        return vectorStore.similaritySearch(SearchRequest.builder().query(request).topK(5).build());
    }

}
