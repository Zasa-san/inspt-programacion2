package inspt_programacion2_kfc.backend.services.users.exceptions;

public class UserCreationFailedException extends UserServiceException {

    public UserCreationFailedException(String message) {
        super(message);
    }

    public UserCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
