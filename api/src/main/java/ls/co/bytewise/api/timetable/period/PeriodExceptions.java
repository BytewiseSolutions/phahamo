package ls.co.bytewise.api.timetable.period;

public class PeriodExceptions {

    public static class PeriodNotFoundException extends RuntimeException {
        public PeriodNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidPeriodException extends RuntimeException {
        public InvalidPeriodException(String message) {
            super(message);
        }
    }
}