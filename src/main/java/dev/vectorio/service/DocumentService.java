package dev.vectorio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vectorio.model.MetadataFields;
import dev.vectorio.model.request.document.Metadata;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatClient ollamaChatClient;
    private final int chunkSize;        // Максимальный размер чанка в символах
    private final int chunkOverlap;     // Размер перекрытия в символах
    private final TokenTextSplitter splitter;
    private final VectorStore pgVectorStore;

    public DocumentService(
            @Qualifier("simpleOllamaRagAdvisor")
            ChatClient simpleOllamaRagAdvisor,
            @Value("${document.service.chunk-size}")
            int chunkSize,
            @Value("${document.service.chunk-overlap}")
            int chunkOverlap,
            VectorStore pgVectorStore
    ) {
        this.ollamaChatClient = simpleOllamaRagAdvisor;
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.pgVectorStore = pgVectorStore;
        this.splitter = new TokenTextSplitter();
    }

    public List<Document> saveDocuments(Metadata metadata, InputStream multipartFile) {
        var documents = splitter.split(createDocumentList(metadata, multipartFile));
        pgVectorStore.add(documents);
        return documents;
    }

    private List<Document> createDocumentList(Metadata metadata, InputStream multipartFile) {
        Tika tika = new Tika();
        var doc = convertToByteArray(multipartFile);
        String mimeType = tika.detect(doc);
        switch (mimeType) {
            case "application/pdf": {
                var reader = new PagePdfDocumentReader(new ByteArrayResource(doc));
                return reader.get();
            }
            case "text/plain": {
                var reader = new TextReader(new ByteArrayResource(doc));
                return reader.get();
            }
            default:
                throw new IllegalArgumentException("file has not valid MIME-type");
        }
    }

    private Map<String, Object> getMetadata(Metadata metadata) {
        var map = new HashMap<String, Object>();
        if (!metadata.name().isEmpty())
            map.put(MetadataFields.NAME.name(), metadata.name());
        if (!metadata.type().isEmpty())
            map.put(MetadataFields.TYPE.name(), metadata.type());
        return map;
    }

    private byte[] convertToByteArray(InputStream multipartFile) {
        try {
            return multipartFile.readAllBytes();
        } catch (IOException exception) {
            log.error("error parse stream to byte array");
            //TODO: создать кастомные эксепшн
            throw new RuntimeException(exception);
        }
    }

}
