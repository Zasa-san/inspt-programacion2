package inspt_programacion2_kfc.backend.exceptions.user;

public class UserCreationFailedException extends UserException {

    public UserCreationFailedException(String message) {
        super(message);
    }

    public UserCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
