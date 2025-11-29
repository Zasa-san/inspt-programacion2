package inspt_programacion2_kfc.frontend.controllers.dto;

import lombok.Data;

@Data
public class CustomizationDto {

    private String id;
    private String nombre;
    private Integer priceModifier;
    private Boolean enabled;
    private String tipo;
    private String grupo;

}
