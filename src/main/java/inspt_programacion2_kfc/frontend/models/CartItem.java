package inspt_programacion2_kfc.frontend.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CartItem {

    private final Producto producto;
    private int quantity;
    private final List<CustomizacionSeleccionada> customizaciones;

    public CartItem(Producto producto, int quantity) {
        this(producto, quantity, new ArrayList<>());
    }

    public CartItem(Producto producto, int quantity, List<CustomizacionSeleccionada> customizaciones) {
        this.producto = producto;
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
        return producto.getPrice() + extras;
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
            return String.valueOf(producto.getId());
        }
        List<Long> ids = customizaciones.stream()
                .map(CustomizacionSeleccionada::getId)
                .sorted()
                .toList();
        return producto.getId() + "_" + ids.toString();
    }

}
