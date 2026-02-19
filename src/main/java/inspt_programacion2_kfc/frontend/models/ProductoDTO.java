package inspt_programacion2_kfc.frontend.models;

import java.util.ArrayList;
import java.util.List;

import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import lombok.Data;

@Data
public class ProductoDTO {

    private final Long id;
    private final String name;
    private final String description;
    private final int precioBase;
    private final String imgUrl;
    private final List<GrupoIngredienteDTO> gruposIngredientes;

    public ProductoDTO(Long id, String name, String description, int precioBase, String imgUrl) {
        this(id, name, description, precioBase, imgUrl, new ArrayList<>());
    }

    public ProductoDTO(Long id, String name, String description, int precioBase, String imgUrl, List<GrupoIngredienteDTO> gruposIngredientes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.precioBase = precioBase;
        this.imgUrl = imgUrl;
        this.gruposIngredientes = gruposIngredientes != null ? gruposIngredientes : new ArrayList<>();
    }

    public boolean tieneGruposIngredientes() {
        return gruposIngredientes != null && !gruposIngredientes.isEmpty();
    }

    /**
     * Define si el producto tiene opciones para que el cliente elija (y por lo
     * tanto conviene mostrar el modal). Si solo hay grupos obligatorios sin
     * variaciones, puede agregarse directo al carrito.
     */
    public boolean tieneOpciones() {
        if (gruposIngredientes == null || gruposIngredientes.isEmpty()) {
            return false;
        }
        return gruposIngredientes.stream().anyMatch(g -> {
            if (g == null) {
                return false;
            }
            if (g.getIngredientes() != null && g.getIngredientes().size() > 1) {
                return true;
            }
            return g.getTipo() != null && !g.getTipo().equalsIgnoreCase("OBLIGATORIO");
        });
    }

}
