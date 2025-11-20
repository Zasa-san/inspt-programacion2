package inspt_programacion2_kfc.backend.exceptions.stock;

public class StockException extends RuntimeException {
    public StockException(String message) {
        super(message);
    }

    public StockException(String message, Throwable cause) {
        super(message, cause);
    }

}
