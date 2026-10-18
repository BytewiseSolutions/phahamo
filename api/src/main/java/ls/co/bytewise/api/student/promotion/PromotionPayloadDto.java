package ls.co.bytewise.api.student.promotion;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PromotionPayloadDto {
    private Long fromClassId;
    private Long toClassId;
    private PromotionAction action;
    private List<Long> studentIds;
    private String reason;
}