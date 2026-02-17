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
import inspt_programacion2_kfc.frontend.models.Customizacion;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;

@Component
public class CartHelper {

    private final ObjectMapper objectMapper;

    public CartHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parsea el JSON de IDs de customizaciones y las convierte a
     * CustomizacionSeleccionada usando los datos actuales del producto.
     */
    public List<CustomizacionSeleccionada> parseCustomizaciones(String json, ProductoDTO productoDTO) {
        List<CustomizacionSeleccionada> result = new ArrayList<>();

        if (json == null || json.trim().isEmpty()) {
            return result;
        }

        try {
            List<String> ids = objectMapper.readValue(json, new TypeReference<List<String>>() {
            });

            for (String customId : ids) {
                // Buscar la customización en el producto
                for (Customizacion c : productoDTO.getCustomizaciones()) {
                    if (c.getId().equals(customId)) {
                        result.add(new CustomizacionSeleccionada(Long.valueOf(c.getId()), c.getNombre(), c.getPriceModifier()));
                        break;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new CartException("Error serializando customizaciones.", e);
        }

        return result;
    }

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
