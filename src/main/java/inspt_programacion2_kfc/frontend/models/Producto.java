package inspt_programacion2_kfc.frontend.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Producto {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String imgUrl;
    private final List<Customizacion> customizaciones;

    public Producto(Long id, String name, String description, int price, String imgUrl) {
        this(id, name, description, price, imgUrl, new ArrayList<>());
    }

    public Producto(Long id, String name, String description, int price, String imgUrl, List<Customizacion> customizaciones) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.customizaciones = customizaciones != null ? customizaciones : new ArrayList<>();
    }

    public boolean tieneCustomizaciones() {
        return customizaciones != null && !customizaciones.isEmpty();
    }

}
