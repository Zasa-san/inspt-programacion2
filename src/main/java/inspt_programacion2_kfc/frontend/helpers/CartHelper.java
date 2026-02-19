package inspt_programacion2_kfc.frontend.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.exceptions.cart.CartException;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;

@Component
public class CartHelper {

    private final ObjectMapper objectMapper;

    public CartHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<CustomizacionSeleccionada> parseCustomizaciones(String customizacionesIdsJson, ProductoDTO productoDTO) {
        if (productoDTO == null) {
            throw new CartException("Producto inválido para agregar al carrito.");
        }

        if (customizacionesIdsJson == null || customizacionesIdsJson.isBlank()) {
            return List.of();
        }

        List<Long> ids;
        try {
            ids = objectMapper.readValue(customizacionesIdsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new CartException("Formato de customizaciones inválido.", e);
        }

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        Map<Long, IngredienteDTO> ingredientesPorId = new HashMap<>();
        Map<Long, String> tipoGrupoPorIngredienteId = new HashMap<>();
        Map<Long, Integer> precioIngredientePorId = new HashMap<>();
        Map<Long, Integer> precioDefaultGrupoUnicoPorIngredienteId = new HashMap<>();
        Set<Long> ingredientesDefaultIds = new HashSet<>();

        List<GrupoIngredienteDTO> grupos = productoDTO.getGruposIngredientes();
        if (grupos != null) {
            for (GrupoIngredienteDTO grupo : grupos) {
                if (grupo == null || grupo.getIngredientes() == null) {
                    continue;
                }

                String tipoGrupo = grupo.getTipo() != null ? grupo.getTipo().toUpperCase() : "";
                Integer precioDefaultGrupoUnico = null;

                if ("OPCIONAL_UNICO".equals(tipoGrupo)) {
                    for (IngredienteDTO ingrediente : grupo.getIngredientes()) {
                        if (ingrediente == null) {
                            continue;
                        }
                        if (Boolean.TRUE.equals(ingrediente.getSeleccionadoPorDefecto())) {
                            int itemPrice = Objects.requireNonNullElse(ingrediente.getItemPrice(), 0);
                            int cantidad = Objects.requireNonNullElse(ingrediente.getCantidad(), 1);
                            precioDefaultGrupoUnico = itemPrice * cantidad;
                            break;
                        }
                    }
                }

                for (IngredienteDTO ingrediente : grupo.getIngredientes()) {
                    if (ingrediente != null && ingrediente.getId() != null) {
                        ingredientesPorId.put(ingrediente.getId(), ingrediente);
                        tipoGrupoPorIngredienteId.put(ingrediente.getId(), tipoGrupo);

                        int itemPrice = Objects.requireNonNullElse(ingrediente.getItemPrice(), 0);
                        int cantidad = Objects.requireNonNullElse(ingrediente.getCantidad(), 1);
                        int precioIngrediente = itemPrice * cantidad;
                        precioIngredientePorId.put(ingrediente.getId(), precioIngrediente);

                        if (Boolean.TRUE.equals(ingrediente.getSeleccionadoPorDefecto())) {
                            ingredientesDefaultIds.add(ingrediente.getId());
                        }

                        if (precioDefaultGrupoUnico != null) {
                            precioDefaultGrupoUnicoPorIngredienteId.put(ingrediente.getId(), precioDefaultGrupoUnico);
                        }
                    }
                }
            }
        }

        List<CustomizacionSeleccionada> customizaciones = new ArrayList<>();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            IngredienteDTO ingrediente = ingredientesPorId.get(id);
            if (ingrediente == null) {
                continue;
            }

            int precioBaseIngrediente = Objects.requireNonNullElse(precioIngredientePorId.get(id), 0);
            String tipoGrupo = tipoGrupoPorIngredienteId.getOrDefault(id, "");

            int precio;
            if (ingredientesDefaultIds.contains(id) || "OBLIGATORIO".equals(tipoGrupo)) {
                precio = 0;
            } else if ("OPCIONAL_UNICO".equals(tipoGrupo)) {
                int precioDefaultGrupo = Objects.requireNonNullElse(precioDefaultGrupoUnicoPorIngredienteId.get(id), 0);
                precio = Math.max(0, precioBaseIngrediente - precioDefaultGrupo);
            } else {
                precio = precioBaseIngrediente;
            }

            String nombre = ingrediente.getItemName() != null ? ingrediente.getItemName() : "Ingrediente";

            customizaciones.add(new CustomizacionSeleccionada(id, nombre, precio));
        }

        return customizaciones;
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
