package ls.co.bytewise.api.reportcard;

public class ReportCardExceptions {

    public static class ReportCardNotFoundException extends RuntimeException {
        public ReportCardNotFoundException(String message) { super(message); }
    }

    public static class ReportCardRenderException extends RuntimeException {
        public ReportCardRenderException(String message, Throwable cause) { super(message, cause); }
    }
}