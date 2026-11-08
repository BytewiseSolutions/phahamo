package ls.co.bytewise.api.legal;

import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

@Service
public class LegalDocumentService {
    private static final long MAX_BYTES = 2 * 1024 * 1024;

    private final LegalDocumentRepository repository;

    public LegalDocumentService(LegalDocumentRepository repository) {
        this.repository = repository;
    }

    public LegalDocumentDto getCurrent(LegalDocumentType type) {
        return new LegalDocumentDto(currentEntity(type));
    }

    public String currentVersion(LegalDocumentType type) {
        return currentEntity(type).getVersion();
    }

    public List<LegalDocumentDto> list() {
        return repository.findAllByOrderByEffectiveAtDesc().stream().map(LegalDocumentDto::new).toList();
    }

    @Transactional
    public LegalDocumentDto publish(LegalDocumentType type, String version, Instant effectiveAt,
                                    MultipartFile file, User publisher) {
        if (type == null || version == null || version.isBlank()) {
            throw new IllegalArgumentException("Document type and version are required");
        }
        if (effectiveAt == null) effectiveAt = Instant.now();
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("A document file is required");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("The document must be 2MB or smaller");
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!(filename.endsWith(".txt") || filename.endsWith(".md")
                || filename.endsWith(".pdf") || filename.endsWith(".docx"))) {
            throw new IllegalArgumentException("Only .txt, .md, .pdf and .docx legal documents are supported");
        }
        String content;
        try {
            byte[] bytes = file.getBytes();
            if (filename.endsWith(".docx")) {
                try (XWPFDocument word = new XWPFDocument(new ByteArrayInputStream(bytes));
                     XWPFWordExtractor extractor = new XWPFWordExtractor(word)) {
                    content = extractor.getText();
                }
            } else if (filename.endsWith(".pdf")) {
                content = "This document is available as a PDF. Use the Open document button to read it.";
            } else {
                content = new String(bytes, StandardCharsets.UTF_8);
            }
            LegalDocument document = new LegalDocument();
            document.setDocumentType(type);
            document.setVersion(version.trim());
            document.setEffectiveAt(effectiveAt);
            document.setContent(content);
            document.setFileData(bytes);
            document.setOriginalFilename(file.getOriginalFilename());
            document.setMimeType(file.getContentType() == null ? mimeType(filename) : file.getContentType());
            document.setCreatedBy(publisher);
            return new LegalDocumentDto(repository.save(document));
        } catch (IOException e) {
            throw new IllegalArgumentException("The document could not be read", e);
        }
    }

    public LegalDocument file(LegalDocumentType type) { return currentEntity(type); }

    private String mimeType(String filename) {
        if (filename.endsWith(".pdf")) return "application/pdf";
        if (filename.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "text/plain";
    }

    private LegalDocument currentEntity(LegalDocumentType type) {
        return repository.findTopByDocumentTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(type, Instant.now())
                .orElseThrow(() -> new IllegalStateException("No published " + type.name().toLowerCase() + " document is available"));
    }
}
