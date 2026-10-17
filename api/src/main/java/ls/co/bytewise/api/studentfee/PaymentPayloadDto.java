package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.payment.PaymentMethodType;

@Getter
@Setter
public class PaymentPayloadDto {

    private Double amount;
    private PaymentMethodType method;
    private String reference;
}