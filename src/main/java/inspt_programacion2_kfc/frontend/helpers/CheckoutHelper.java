package inspt_programacion2_kfc.frontend.helpers;

import java.util.List;

import org.springframework.stereotype.Component;

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;

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
}
