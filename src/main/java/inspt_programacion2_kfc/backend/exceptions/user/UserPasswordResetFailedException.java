package inspt_programacion2_kfc.backend.exceptions.user;

public class UserPasswordResetFailedException extends UserException {

    public UserPasswordResetFailedException(String message) {
        super(message);
    }

    public UserPasswordResetFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
