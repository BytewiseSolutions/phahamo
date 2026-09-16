package ls.co.bytewise.api.timetable;

public class TimetableExceptions {

    public static class TimetableNotFoundException extends RuntimeException {
        public TimetableNotFoundException(String message) {
            super(message);
        }
    }

    public static class TimetableConflictException extends RuntimeException {
        public TimetableConflictException(String message) {
            super(message);
        }
    }
}