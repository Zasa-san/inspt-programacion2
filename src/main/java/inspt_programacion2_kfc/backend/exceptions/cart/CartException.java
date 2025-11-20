package inspt_programacion2_kfc.backend.exceptions.cart;

public class CartException extends RuntimeException {
    public CartException(String message) {
        super(message);
    }
    public CartException(String message, Throwable cause) {
        super(message, cause);
    }

}
