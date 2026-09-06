package ls.co.bytewise.api.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseDto {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;
    private Long updatedBy;

    protected BaseDto(BaseEntity entity) {
        this.id = entity.getId();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();

        if (entity.getCreatedBy() != null) {
            this.createdBy = entity.getCreatedBy().getId();
        }

        if (entity.getUpdatedBy() != null) {
            this.updatedBy = entity.getUpdatedBy().getId();
        }
    }
}