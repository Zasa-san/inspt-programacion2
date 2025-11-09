package inspt_programacion2_kfc.service;

public class UserAlreadyExistsException extends UserServiceException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
