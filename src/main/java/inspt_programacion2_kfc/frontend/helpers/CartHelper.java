package inspt_programacion2_kfc.frontend.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.exceptions.cart.CartException;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;

@Component
public class CartHelper {

    private final ObjectMapper objectMapper;

    public CartHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // parseCustomizaciones eliminado: el proyecto dejÃ³ de usar el modelo viejo de Customizacion.

    /**
     * Calcula la cantidad total de un producto en el carrito, sumando todas las
     * variantes (diferentes customizaciones).
     */
    public int calcularCantidadProductoEnCarrito(Map<String, CartItem> cart, Long productId) {
        return cart.values().stream()
                .filter(item -> item.getProductoDTO().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
