package inspt_programacion2_kfc.frontend.models.productos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una customización elegida por el usuario al agregar al carrito.
 * Guarda los datos al momento de la selección para histórico.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomizacionSeleccionada {

    private Long id;
    private String nombre;
    private int precio; // precio en centavos al momento de selección

}

