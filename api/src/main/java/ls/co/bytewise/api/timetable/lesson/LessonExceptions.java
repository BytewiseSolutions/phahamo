package ls.co.bytewise.api.timetable.lesson;

public class LessonExceptions {

    public static class LessonNotFoundException extends RuntimeException {
        public LessonNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidLessonException extends RuntimeException {
        public InvalidLessonException(String message) {
            super(message);
        }
    }

    public static class ActiveSchoolNotFoundException extends RuntimeException {
        public ActiveSchoolNotFoundException(String message) {
            super(message);
        }
    }
}