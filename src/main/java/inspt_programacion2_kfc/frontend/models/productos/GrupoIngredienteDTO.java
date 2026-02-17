package inspt_programacion2_kfc.frontend.models.productos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir datos de grupos de ingredientes desde el cliente. Representa
 * la estructura de un grupo durante el CRUD de productos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoIngredienteDTO {

    private String nombre;
    private String tipo;
    private Integer minSeleccion;
    private Integer maxSeleccion;
    private List<IngredienteDTO> ingredientes;

}
