package inspt_programacion2_kfc.backend.models.orders;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO usado por el backend para crear pedidos a partir del carrito,
 * desacoplado del modelo de frontend.
 */
@Data
@AllArgsConstructor
public class CartItemDto {

    private Long productoId;
    private int quantity;
    private String productoName;
}


