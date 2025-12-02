package inspt_programacion2_kfc.frontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO unificado para manejar customizaciones en el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customizacion {

    private String id;
    private String nombre;
    private Integer priceModifier;
    private Boolean enabled;
    private String tipo;
    private String grupo;

}
