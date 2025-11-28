package inspt_programacion2_kfc.frontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO simple para mostrar customizaciones disponibles en el frontend.
 */
@Data
@AllArgsConstructor
public class Customizacion {

    private final Long id;
    private final String nombre;
    private final int priceModifier; // en centavos

}

