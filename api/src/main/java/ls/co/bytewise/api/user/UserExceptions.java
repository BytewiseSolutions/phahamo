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

    public static class LastAdminException extends RuntimeException {
        public LastAdminException(String message) {
            super(message);
        }
    }

    public static class AdminProtectionException extends RuntimeException {
        public AdminProtectionException(String message) {
            super(message);
        }
    }
}