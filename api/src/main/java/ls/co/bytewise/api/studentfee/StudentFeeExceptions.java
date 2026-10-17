package ls.co.bytewise.api.studentfee;

public class StudentFeeExceptions {

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) { super(message); }
    }

    public static class InvalidFeeOperationException extends RuntimeException {
        public InvalidFeeOperationException(String message) { super(message); }
    }
}