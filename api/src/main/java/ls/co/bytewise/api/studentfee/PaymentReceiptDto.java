package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PaymentReceiptDto {

    private String schoolName;
    private String schoolAddress;
    private String schoolPhone;
    private String schoolEmail;
    private byte[] schoolLogo;
    private String schoolLogoMimeType;

    private String receiptNumber;
    private String studentName;
    private String admissionNumber;
    private String className;

    private Double amountPaid;
    private String paymentMethod;
    private String reference;
    private LocalDate paymentDate;
    private Double balanceAfter;

    private String receivedBy;
    private LocalDate generatedDate;
}