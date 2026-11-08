package ls.co.bytewise.api.legal;

import java.time.Instant;

public record LegalDocumentDto(
        Long id,
        LegalDocumentType documentType,
        String version,
        String content,
        Instant effectiveAt
) {
    public LegalDocumentDto(LegalDocument document) {
        this(document.getId(), document.getDocumentType(), document.getVersion(),
                document.getContent(), document.getEffectiveAt());
    }
}
