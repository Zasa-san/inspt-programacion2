package inspt_programacion2_kfc.backend.exceptions.order;

public class OrderAlreadyDeliveredException extends OrderException {
    public OrderAlreadyDeliveredException(String message) {
        super(message);
    }
    public OrderAlreadyDeliveredException(String message, Throwable cause) {
        super(message, cause);
    }

}
