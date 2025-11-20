package inspt_programacion2_kfc.backend.exceptions.cart;

public class CartEmptyException extends CartException {
    public CartEmptyException(String message) {
        super(message);
    }
    public CartEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
