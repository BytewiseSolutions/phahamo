package ls.co.bytewise.api.user;

public class UserExceptions {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidRoleAssignmentException extends RuntimeException {
        public InvalidRoleAssignmentException(String message) {
            super(message);
        }
    }
}