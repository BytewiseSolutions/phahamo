package ls.co.bytewise.api.studentfee;

public class StudentFeeExceptions {

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) { super(message); }
    }

    public static class InvalidFeeOperationException extends RuntimeException {
        public InvalidFeeOperationException(String message) { super(message); }
    }

    public static class DuplicatePaymentException extends RuntimeException {
        private final StudentFeePaymentDto existingPayment;

        public DuplicatePaymentException(StudentFeePaymentDto existingPayment) {
            super("A similar payment has already been recorded");
            this.existingPayment = existingPayment;
        }

        public StudentFeePaymentDto getExistingPayment() { return existingPayment; }
    }
}
