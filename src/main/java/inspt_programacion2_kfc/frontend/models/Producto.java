package inspt_programacion2_kfc.frontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Producto {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;

}


