package inspt_programacion2_kfc.frontend.helpers;

import java.util.List;

import org.springframework.stereotype.Component;

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.frontend.models.carrito.CartItem;
import inspt_programacion2_kfc.frontend.models.productos.CustomizacionSeleccionada;

@Component
public class CheckoutHelper {

    /**
     * Convierte CartItem a CartItemDto incluyendo customizaciones.
     */
    public CartItemDto toCartItemDto(CartItem ci) {
        List<Long> ingredientesIds = null;
        if (ci.getCustomizaciones() != null && !ci.getCustomizaciones().isEmpty()) {
            ingredientesIds = ci.getCustomizaciones().stream()
                    .map(CustomizacionSeleccionada::getId)
                    .toList();
        }

        return new CartItemDto(
                ci.getProducto().getId(),
                ci.getQuantity(),
                ci.getProducto().getName(),
                ci.getPrecioUnitario(),
                ingredientesIds
        );
    }

    public String normalizarNombreCliente(String customerName) {
        if (customerName == null) {
            return null;
        }
        String limpio = customerName.trim();
        if (limpio.isEmpty() || limpio.length() > 80) {
            return null;
        }
        return limpio;
    }
}
