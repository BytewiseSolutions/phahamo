package ls.co.bytewise.api.fee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FeeItemDto {

    private Long id;
    private String name;
    private Double amount;
    private Boolean mandatory = true;
    private String description;

    public FeeItemDto(FeeItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.amount = item.getAmount();
        this.mandatory = item.getMandatory();
        this.description = item.getDescription();
    }
}