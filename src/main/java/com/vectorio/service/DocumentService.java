package com.vectorio.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    public List<Document> createDocumentList(MultipartFile multipartFile) {
        return createChunks(checkPdf(multipartFile));

    }

    private List<Document> createChunks(byte[] pdfByteArray) {
        var chunks = new ArrayList<Document>();
        try (var pdfDocument = Loader.loadPDF(pdfByteArray)) {
            var stripper = new PDFTextStripper();
            var pagesCount = pdfDocument.getNumberOfPages();
        } catch (IOException exception) {
            log.error("Ошибка ввода вывода при создании чанков");
            throw new RuntimeException(exception);
        }
        return chunks;
    }

    private byte[] checkPdf(MultipartFile multipartFile) {
        var magic = "%PDF-".getBytes();
        byte[] fileHead;
        byte[] pdfFile = null;
        try {
            var multipatrtInputStream = multipartFile.getInputStream();
            fileHead = multipatrtInputStream.readNBytes(magic.length);
            if (Arrays.equals(magic, fileHead))
                pdfFile = multipartFile.getBytes();
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
