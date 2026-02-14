package inspt_programacion2_kfc.backend.exceptions.stock;

import java.util.List;

public class ItemInUseException extends RuntimeException {

    private final List<String> productosEnUso;

    public ItemInUseException(String message, List<String> productosEnUso) {
        super(message);
        this.productosEnUso = productosEnUso;
    }

    public List<String> getProductosEnUso() {
        return productosEnUso;
    }

}
