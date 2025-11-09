package inspt_programacion2_kfc.service;

public class UserCreationFailedException extends UserServiceException {

    public UserCreationFailedException(String message) {
        super(message);
    }

    public UserCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
