package inspt_programacion2_kfc.backend.models.dto.order;

import lombok.Data;

@Data
public class CartItemDto {

    private Long productoId;
    private int quantity;
    private String productoName;
    
    /**
     * Precio unitario calculado (precio base + extras) en centavos.
     */
    private int precioUnitario;
    
    /**
     * JSON con las customizaciones seleccionadas.
     */
    private String customizacionesJson;

    public CartItemDto(Long productoId, int quantity, String productoName) {
        this(productoId, quantity, productoName, 0, null);
    }

    public CartItemDto(Long productoId, int quantity, String productoName, int precioUnitario, String customizacionesJson) {
        this.productoId = productoId;
        this.quantity = quantity;
        this.productoName = productoName;
        this.precioUnitario = precioUnitario;
        this.customizacionesJson = customizacionesJson;
    }

}
