package inspt_programacion2_kfc.frontend.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProductoDTO {

    private final Long id;
    private final String name;
    private final String description;
    private final int precioBase;
    private final String imgUrl;
    private final List<Customizacion> customizaciones;

    public ProductoDTO(Long id, String name, String description, int precioBase, String imgUrl) {
        this(id, name, description, precioBase, imgUrl, new ArrayList<>());
    }

    public ProductoDTO(Long id, String name, String description, int precioBase, String imgUrl, List<Customizacion> customizaciones) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.precioBase = precioBase;
        this.imgUrl = imgUrl;
        this.customizaciones = customizaciones != null ? customizaciones : new ArrayList<>();
    }

    public boolean tieneCustomizaciones() {
        return customizaciones != null && !customizaciones.isEmpty();
    }

}
