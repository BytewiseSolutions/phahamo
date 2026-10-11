package ls.co.bytewise.api.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileDto {

    private Long id;
    private String name;
    private String type;
    private Long fileSize;

    public FileDto(DataFile file) {
        this.id = file.getId();
        this.name = file.getName();
        this.type = file.getType();
        this.fileSize = file.getFileSize();
    }
}