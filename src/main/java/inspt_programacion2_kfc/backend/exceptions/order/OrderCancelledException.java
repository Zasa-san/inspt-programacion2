package inspt_programacion2_kfc.backend.exceptions.order;

public class OrderCancelledException extends OrderException {
    public OrderCancelledException(String message) {
        super(message);
    }
    public OrderCancelledException(String message, Throwable cause) {
        super(message, cause);
    }

}
