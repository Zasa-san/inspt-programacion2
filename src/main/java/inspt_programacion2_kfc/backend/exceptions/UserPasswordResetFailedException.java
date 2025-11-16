package inspt_programacion2_kfc.backend.exceptions;

public class UserPasswordResetFailedException extends UserServiceException {

    public UserPasswordResetFailedException(String message) {
        super(message);
    }

    public UserPasswordResetFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
