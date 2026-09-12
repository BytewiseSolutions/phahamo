package ls.co.bytewise.api.subject.subjectclassassignment;

public final class SubjectClassAssignmentExceptions {

    private SubjectClassAssignmentExceptions() {}

    public static class AssignmentNotFoundException extends RuntimeException {
        public AssignmentNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidAssignmentException extends RuntimeException {
        public InvalidAssignmentException(String message) {
            super(message);
        }
    }

    public static class DuplicateAssignmentException extends RuntimeException {
        public DuplicateAssignmentException(String message) {
            super(message);
        }
    }
}