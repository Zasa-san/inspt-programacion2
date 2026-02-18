package inspt_programacion2_kfc.frontend.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CartItem {

    private final ProductoDTO productoDTO;
    private int quantity;
    private final List<CustomizacionSeleccionada> customizaciones;

    // Compatibilidad con templates/helpers viejos que referencian "producto".
    public ProductoDTO getProducto() {
        return productoDTO;
    }

    public CartItem(ProductoDTO productoDTO, int quantity) {
        this(productoDTO, quantity, new ArrayList<>());
    }

    public CartItem(ProductoDTO productoDTO, int quantity, List<CustomizacionSeleccionada> customizaciones) {
        this.productoDTO = productoDTO;
        this.quantity = quantity;
        this.customizaciones = customizaciones != null ? customizaciones : new ArrayList<>();
    }

    public void increment(int amount) {
        this.quantity += amount;
        if (this.quantity < 1) {
            this.quantity = 1;
        }
    }

    /**
     * Calcula el precio unitario: precio base + suma de customizaciones
     */
    public int getPrecioUnitario() {
        int extras = customizaciones.stream()
                .mapToInt(CustomizacionSeleccionada::getPrecio)
                .sum();
        return productoDTO.getPrecioBase() + extras;
    }

    /**
     * Subtotal = precio unitario * cantidad
     */
    public int getSubtotal() {
        return getPrecioUnitario() * quantity;
    }

    /**
     * Genera una clave única para identificar este item en el carrito.
     * Combina el producto ID con los IDs de customizaciones ordenados.
     */
    public String getCartKey() {
        if (customizaciones == null || customizaciones.isEmpty()) {
            return String.valueOf(productoDTO.getId());
        }
        List<Long> ids = customizaciones.stream()
                .map(CustomizacionSeleccionada::getId)
                .sorted()
                .toList();
        return productoDTO.getId() + "_" + ids;
    }

}
