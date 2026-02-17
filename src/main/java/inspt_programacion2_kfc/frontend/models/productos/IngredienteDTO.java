package inspt_programacion2_kfc.frontend.models.productos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir datos de ingredientes desde el cliente. Representa un
 * ingrediente dentro de un grupo lors del CRUD de productos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteDTO {

    private Long itemId;
    private Integer cantidad;
    private Boolean seleccionadoPorDefecto;

}
