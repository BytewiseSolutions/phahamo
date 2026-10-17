package ls.co.bytewise.api.fee;

public class FeeExceptions {

    public static class FeeStructureNotFoundException extends RuntimeException {
        public FeeStructureNotFoundException(String message) {
            super(message);
        }
    }

    public static class AnnualFeeNotFoundException extends RuntimeException {
        public AnnualFeeNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidFeeOperationException extends RuntimeException {
        public InvalidFeeOperationException(String message) {
            super(message);
        }
    }
}