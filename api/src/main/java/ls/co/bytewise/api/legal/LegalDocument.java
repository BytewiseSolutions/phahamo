package ls.co.bytewise.api.legal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import ls.co.bytewise.api.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "legal_document")
public class LegalDocument extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 16)
    private LegalDocumentType documentType;

    @Column(nullable = false, length = 64)
    private String version;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "effective_at", nullable = false)
    private Instant effectiveAt;
}
