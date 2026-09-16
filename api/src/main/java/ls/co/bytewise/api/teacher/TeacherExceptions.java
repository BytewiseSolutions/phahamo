package ls.co.bytewise.api.teacher;

public final class TeacherExceptions {

    private TeacherExceptions() {
    }

    public static class TeacherNotFoundException extends RuntimeException {

        public TeacherNotFoundException(String message) {
            super(message);
        }
    }

    public static class TeacherAlreadyExistsException extends RuntimeException {

        public TeacherAlreadyExistsException(String message) {
            super(message);
        }
    }
}