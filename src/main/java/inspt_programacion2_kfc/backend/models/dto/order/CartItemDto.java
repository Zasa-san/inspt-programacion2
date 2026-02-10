package inspt_programacion2_kfc.backend.models.dto.order;

import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import lombok.Data;

import java.util.List;

@Data
public class CartItemDto {

    private Long productoId;
    private int quantity;
    private String productoName;
    
    /**
     * Precio unitario calculado (precio base + extras) en centavos.
     */
    private int precioUnitario;

    private List<GrupoIngrediente> customizaciones;

    public CartItemDto(Long productoId, int quantity, String productoName) {
        this(productoId, quantity, productoName, 0, null);
    }

    public CartItemDto(Long productoId, int quantity, String productoName, int precioUnitario, List<GrupoIngrediente> customizaciones) {
        this.productoId = productoId;
        this.quantity = quantity;
        this.productoName = productoName;
        this.precioUnitario = precioUnitario;
        this.customizaciones = customizaciones;
    }

}
