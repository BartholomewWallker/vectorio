package com.vectorio.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectorio.model.request.document.Metadata;
import com.vectorio.model.MetadataFields;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatClient ollamaChatClient;
    private static final String SYSTEM_PROMPT = "Раздели следующий текст на чанки по 500 символов, стараясь не разрывать предложения: ";
    private final int chunkSize;        // Максимальный размер чанка в символах
    private final int chunkOverlap;     // Размер перекрытия в символах

    public DocumentService(
            ChatClient simpleOllamaRagAdvisor,
            @Value("${document.service.chunk-size}")
            int chunkSize,
            @Value("${document.service.chunk-overlap}")
            int chunkOverlap
    ) {
        this.ollamaChatClient = simpleOllamaRagAdvisor;
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    public List<Document> createDocumentList(Metadata metadata, InputStream multipartFile) {
        return createChunks(metadata,checkPdf(multipartFile));
    }

    private List<Document> createChunks(Metadata metadata, byte[] pdfByteArray) {
        var chunks = new ArrayList<Document>();
        try (var pdfDocument = Loader.loadPDF(pdfByteArray)) {
            var stripper = new PDFTextStripper();
            var metadataMap = getMetadata(metadata);

            var text = stripper.getText(pdfDocument);
            var paragraphs = text.split("\\r?\\n\\r?\\n");
            for (String paragraph:paragraphs){
               if (paragraph.length()>chunkSize) {
                    chunks.addAll(splitToSentences(metadataMap, paragraph));
               }else {
                   log.info("Будет добавлен следующий чанк: {}", paragraph);
                   chunks.add(new Document(paragraph, metadataMap));
               }
            }

        } catch (IOException exception) {
            log.error("Ошибка ввода вывода при создании чанков");
            throw new RuntimeException(exception);
        }
        return chunks;
    }

    private List<Document> splitToSentences(Map<String,Object> metadata, String paragraph) throws JsonProcessingException {
        List<Document> sentences = new ArrayList<>();
        log.info("На вход поступил параграф следущего содержимого: {}", paragraph);
        log.debug("Системный промпт: {}", SYSTEM_PROMPT);
        var result =  ollamaChatClient
                .prompt(SYSTEM_PROMPT+paragraph)
                .call().entity(new ListOutputConverter(new DefaultConversionService()));
        log.info("Получили ответ от AI {}",result);
        /*String [] array = objectMapper.readValue(result, String[].class);
        var sentencesResponse = Arrays.asList(array);
        if (sentencesResponse!=null)
            sentencesResponse.forEach(e->{
                log.info("AI добавит следующий чанк: {}", e);
                sentences.add(new Document(e, metadata));
            });
        else
            log.error("Ошибка получения документов. Локальная модель не запущена или не отправила ответ");*/
        return sentences;
    }

    private Map<String,Object> getMetadata(Metadata metadata) {
        var map = new HashMap<String,Object>();
        if (!metadata.name().isEmpty())
            map.put(MetadataFields.NAME.name(), metadata.name());
        if (!metadata.type().isEmpty())
            map.put(MetadataFields.TYPE.name(), metadata.type());
        return map;
    }

    private byte[] checkPdf(InputStream multipartFile) {
        var magic = "%PDF-".getBytes();
        byte[] fileHead;
        byte[] pdfFile = null;
        try {
            fileHead = multipartFile.readNBytes(magic.length);
            if (Arrays.equals(magic, fileHead))
                pdfFile = multipartFile.readAllBytes();
        } catch (IOException exception) {
            log.error("что-то не так с файлом для загрузки в RAG");
            throw new RuntimeException(exception);
        }
        if (pdfFile == null) {
            throw new IllegalArgumentException("File is not PDF document");
        }
        return pdfFile;
    }
}
