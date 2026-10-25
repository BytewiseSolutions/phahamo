package ls.co.bytewise.api.holiday;

public class HolidayExceptions {
    public static class HolidayNotFoundException extends RuntimeException {
        public HolidayNotFoundException(String message) { super(message); }
    }

    public static class InvalidHolidayException extends RuntimeException {
        public InvalidHolidayException(String message) { super(message); }
    }
}
