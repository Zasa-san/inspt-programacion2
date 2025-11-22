package inspt_programacion2_kfc.backend.exceptions.product;

public class ProductImageException extends ProductException {

    public ProductImageException(String message) {
        super(message);
    }

    public ProductImageException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
