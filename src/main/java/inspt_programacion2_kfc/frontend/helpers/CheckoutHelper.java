package inspt_programacion2_kfc.frontend.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspt_programacion2_kfc.backend.exceptions.cart.CartException;
import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.frontend.models.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CheckoutHelper {

    private final ObjectMapper objectMapper;

    public CheckoutHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * Convierte CartItem a CartItemDto incluyendo customizaciones.
     */
    public CartItemDto toCartItemDto(CartItem ci) {
        String customizacionesJson = null;

        if (ci.getCustomizaciones() != null && !ci.getCustomizaciones().isEmpty()) {
            try {
                customizacionesJson = objectMapper.writeValueAsString(ci.getCustomizaciones());
            } catch (JsonProcessingException e) {
                throw new CartException("Error serializando customizaciones.", e);
            }
        }

        return new CartItemDto(
                ci.getProducto().getId(),
                ci.getQuantity(),
                ci.getProducto().getName(),
                ci.getPrecioUnitario(),
                customizacionesJson
        );
    }
}
