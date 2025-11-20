package inspt_programacion2_kfc.backend.exceptions.order;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
