package ls.co.bytewise.api.legal;

import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

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
        if (!(filename.endsWith(".txt") || filename.endsWith(".md"))) {
            throw new IllegalArgumentException("Only .txt and .md legal documents are supported");
        }
        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("The document could not be read", e);
        }
        if (content.isBlank()) throw new IllegalArgumentException("The document cannot be empty");

        LegalDocument document = new LegalDocument();
        document.setDocumentType(type);
        document.setVersion(version.trim());
        document.setEffectiveAt(effectiveAt);
        document.setContent(content);
        document.setCreatedBy(publisher);
        return new LegalDocumentDto(repository.save(document));
    }

    private LegalDocument currentEntity(LegalDocumentType type) {
        return repository.findTopByDocumentTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(type, Instant.now())
                .orElseThrow(() -> new IllegalStateException("No published " + type.name().toLowerCase() + " document is available"));
    }
}
