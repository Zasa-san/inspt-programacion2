package inspt_programacion2_kfc.frontend.helpers;

import java.util.List;

import org.springframework.stereotype.Component;

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.frontend.models.CartItem;

@Component
public class CheckoutHelper {

    /**
     * Convierte CartItem a CartItemDto incluyendo ingredientesIds.
     */
    public CartItemDto toCartItemDto(CartItem ci) {
        List<Long> ingredientesIds = ci.getIngredientesIds();

        return new CartItemDto(
                ci.getProducto().getId(),
                ci.getQuantity(),
                ci.getProducto().getName(),
                ci.getPrecioUnitario(),
                ingredientesIds
        );
    }
}
