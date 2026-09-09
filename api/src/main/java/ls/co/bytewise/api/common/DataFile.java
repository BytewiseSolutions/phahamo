package ls.co.bytewise.api.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "data_file")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataFile extends BaseEntity {
    private String name;
    private String type;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    @ToString.Exclude
    private byte[] contents;

    @Column(name = "file_size")
    private Long fileSize;

    public String getOriginalFileName() {
        return this.name;
    }

    public String getContentType() {
        return this.type;
    }

    public Long getFileSize() {
        if (this.fileSize == null && this.contents != null) {
            return (long) this.contents.length;
        }
        return this.fileSize;
    }
}