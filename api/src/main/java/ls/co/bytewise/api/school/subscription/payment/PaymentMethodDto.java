package ls.co.bytewise.api.school.subscription.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentMethodDto {

    private Long id;
    private PaymentMethodType type;
    private String label;
    private String details;
    private Boolean active;
    private int sortOrder;

    public PaymentMethodDto(PaymentMethod method) {
        this.id = method.getId();
        this.type = method.getType();
        this.label = method.getLabel();
        this.details = method.getDetails();
        this.active = method.isActive();
        this.sortOrder = method.getSortOrder();
    }
}