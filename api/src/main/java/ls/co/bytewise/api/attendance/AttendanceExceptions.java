package ls.co.bytewise.api.attendance;

public class AttendanceExceptions {

    public static class SchoolClassNotFoundException extends RuntimeException {
        public SchoolClassNotFoundException(String message) {
            super(message);
        }
    }

    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }

    public static class NotClassTeacherException extends RuntimeException {
        public NotClassTeacherException(String message) {
            super(message);
        }
    }
}