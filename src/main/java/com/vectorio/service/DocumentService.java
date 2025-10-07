package com.vectorio.service;

import com.vectorio.model.Metadata;
import com.vectorio.model.MetadataFields;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final int chunkSize;        // Максимальный размер чанка в символах
    private final int chunkOverlap;     // Размер перекрытия в символах

    public DocumentService(
            @Value("${document.service.chunk-size}")
            int chunkSize,
            @Value("${document.service.chunk-overlap}")
            int chunkOverlap
    ) {
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
            var pagesCount = pdfDocument.getNumberOfPages();
            var metadataMap = getMetadata(metadata);

            var text = stripper.getText(pdfDocument);
            var paragraphs = text.split("\\r?\\n\\r?\\n");
            for (String paragraph:paragraphs){
               if (paragraph.length()>chunkSize) {
                    chunks.addAll(splitToSentences(metadataMap, paragraph));
               }else {
                   chunks.add(new Document(paragraph, metadataMap));
               }
            }


        } catch (IOException exception) {
            log.error("Ошибка ввода вывода при создании чанков");
            throw new RuntimeException(exception);
        }
        return chunks;
    }

    private List<Document> splitToSentences(Map<String,Object> metadata, String paragraph) {
        List<Document> sentences = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^.!?]+[.!?]?");
        Matcher matcher = pattern.matcher(paragraph);
        while (matcher.find()) {
            sentences.add(new Document(matcher.group().trim(), metadata));
        }
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
