package inspt_programacion2_kfc.frontend.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;

@Component
public class CartHelper {

    private final ObjectMapper objectMapper;

    public CartHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public List<Long> parseIngredientesIdsJson(String ingredientesIdsJson) {
        if (StringUtils.isBlank(ingredientesIdsJson)) {
            return List.of();
        }

        try {
            List<Long> ids = objectMapper.readValue(ingredientesIdsJson, new TypeReference<>() {});
            if (ids == null) {
                return List.of();
            }
            Set<Long> unique = new HashSet<>();
            for (Long id : ids) {
                if (id != null) {
                    unique.add(id);
                }
            }
            return new ArrayList<>(unique);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    /**
     * Completa la selección de ingredientes para que siempre se envíen ids,
     * tomando defaults cuando faltan (obligatorios y opcionales por defecto).
     *
     * Valida que los ids provistos pertenezcan al producto.
     */
    public List<Long> completarIngredientesSeleccionados(ProductoDTO producto, List<Long> requestedIds) {
        if (producto == null || producto.getGruposIngredientes() == null) {
            return List.of();
        }

        List<GrupoIngredienteDTO> grupos = producto.getGruposIngredientes();
        Map<Long, IngredienteDTO> ingredientePorId = new HashMap<>();
        for (GrupoIngredienteDTO g : grupos) {
            if (g == null || g.getIngredientes() == null) {
                continue;
            }
            for (IngredienteDTO ing : g.getIngredientes()) {
                if (ing != null && ing.getId() != null) {
                    ingredientePorId.put(ing.getId(), ing);
                }
            }
        }

        if (requestedIds != null) {
            for (Long id : requestedIds) {
                if (id == null) {
                    continue;
                }
                if (!ingredientePorId.containsKey(id)) {
                    throw new IllegalArgumentException("Ingrediente inválido para el producto.");
                }
            }
        }

        Set<Long> seleccion = new LinkedHashSet<>();
        Set<Long> requestedSet = requestedIds != null ? new HashSet<>(requestedIds) : Set.of();

        for (GrupoIngredienteDTO g : grupos) {
            if (g == null || g.getIngredientes() == null || g.getIngredientes().isEmpty()) {
                continue;
            }

            String tipo = g.getTipo() != null ? g.getTipo().trim().toUpperCase() : "";
            boolean esObligatorio = "OBLIGATORIO".equals(tipo);
            boolean esOpcionalUnico = "OPCIONAL_UNICO".equals(tipo);
            boolean esUnico = esObligatorio || esOpcionalUnico;

            List<IngredienteDTO> ingredientes = g.getIngredientes();
            List<Long> candidatos = new ArrayList<>();
            for (IngredienteDTO ing : ingredientes) {
                if (ing == null || ing.getId() == null) {
                    continue;
                }
                if (requestedSet.contains(ing.getId())) {
                    candidatos.add(ing.getId());
                }
            }

            if (esUnico) {
                Long elegido = null;
                if (!candidatos.isEmpty()) {
                    elegido = candidatos.get(0);
                } else {
                    for (IngredienteDTO ing : ingredientes) {
                        if (ing != null && Boolean.TRUE.equals(ing.getSeleccionadoPorDefecto()) && ing.getId() != null) {
                            elegido = ing.getId();
                            break;
                        }
                    }
                    // En OPCIONAL_UNICO sin default se permite "sin selección".
                    // En OBLIGATORIO sin default se selecciona el primero.
                    if (elegido == null && esObligatorio) {
                        IngredienteDTO first = ingredientes.get(0);
                        if (first != null) {
                            elegido = first.getId();
                        }
                    }
                }
                if (elegido != null) {
                    seleccion.add(elegido);
                }
            } else {
                if (!candidatos.isEmpty()) {
                    seleccion.addAll(candidatos);
                } else {
                    for (IngredienteDTO ing : ingredientes) {
                        if (ing != null && Boolean.TRUE.equals(ing.getSeleccionadoPorDefecto()) && ing.getId() != null) {
                            seleccion.add(ing.getId());
                        }
                    }
                }
            }
        }

        return new ArrayList<>(seleccion);
    }

    /**
     * Acumula en {@code out} los requerimientos de stock por Item (no Ingrediente)
     * para un CartItem, multiplicando por la cantidad del producto.
     */
    public void acumularRequeridosPorItemId(Map<Long, Integer> out, CartItem cartItem) {
        if (out == null || cartItem == null || cartItem.getProductoDTO() == null || cartItem.getProductoDTO().getGruposIngredientes() == null) {
            return;
        }

        Map<Long, IngredienteDTO> ingredientePorId = new LinkedHashMap<>();
        for (GrupoIngredienteDTO g : cartItem.getProductoDTO().getGruposIngredientes()) {
            if (g == null || g.getIngredientes() == null) {
                continue;
            }
            for (IngredienteDTO ing : g.getIngredientes()) {
                if (ing != null && ing.getId() != null) {
                    ingredientePorId.put(ing.getId(), ing);
                }
            }
        }

        for (Long ingredienteId : cartItem.getIngredientesIds()) {
            IngredienteDTO ing = ingredientePorId.get(ingredienteId);
            if (ing == null || ing.getItemId() == null) {
                continue;
            }
            int cant = ing.getCantidad() != null ? ing.getCantidad() : 1;
            int requerido = cant * cartItem.getQuantity();
            out.merge(ing.getItemId(), requerido, Integer::sum);
        }
    }

    public int calcularCantidadProductoEnCarrito(Map<String, CartItem> cart, Long productId) {
        return cart.values().stream()
                .filter(item -> item.getProductoDTO().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
