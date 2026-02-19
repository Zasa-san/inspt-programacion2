package inspt_programacion2_kfc.frontend.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;
import lombok.Data;

@Data
public class CartItem {

    private final ProductoDTO productoDTO;
    private int quantity;
    private final List<Long> ingredientesIds;

    // Compatibilidad con templates/helpers viejos que referencian "producto".
    public ProductoDTO getProducto() {
        return productoDTO;
    }

    public CartItem(ProductoDTO productoDTO, int quantity) {
        this(productoDTO, quantity, new ArrayList<>());
    }

    public CartItem(ProductoDTO productoDTO, int quantity, List<Long> ingredientesIds) {
        this.productoDTO = productoDTO;
        this.quantity = quantity;
        this.ingredientesIds = ingredientesIds != null ? ingredientesIds : new ArrayList<>();
    }

    public void increment(int amount) {
        this.quantity += amount;
        if (this.quantity < 1) {
            this.quantity = 1;
        }
    }

    /**
     * Precio unitario para mostrar en carrito: precioBase + extras.
     *
     * "Extras" = max(0, totalSeleccionado - totalDefaults), para evitar mostrar
     * negativos cuando el usuario elige opciones más baratas o "sin selección".
     */
    public int getPrecioUnitario() {
        if (productoDTO == null || productoDTO.getGruposIngredientes() == null) {
            return 0;
        }

        int totalSeleccionado = 0;
        Map<Long, IngredienteDTO> ingredientePorId = new LinkedHashMap<>();
        for (var grupo : productoDTO.getGruposIngredientes()) {
            if (grupo == null || grupo.getIngredientes() == null) {
                continue;
            }
            for (var ing : grupo.getIngredientes()) {
                if (ing != null && ing.getId() != null) {
                    ingredientePorId.put(ing.getId(), ing);
                }
            }
        }
        for (Long ingId : ingredientesIds) {
            IngredienteDTO ing = ingredientePorId.get(ingId);
            totalSeleccionado += precioIngrediente(ing);
        }

        int totalDefaults = 0;
        for (var grupo : productoDTO.getGruposIngredientes()) {
            if (grupo == null || grupo.getIngredientes() == null || grupo.getIngredientes().isEmpty()) {
                continue;
            }

            String tipo = grupo.getTipo() != null ? grupo.getTipo().trim().toUpperCase() : "";
            boolean esObligatorio = "OBLIGATORIO".equals(tipo);
            boolean esOpcionalUnico = "OPCIONAL_UNICO".equals(tipo);

            if (esObligatorio) {
                List<IngredienteDTO> defaults = defaults(grupo.getIngredientes());
                List<IngredienteDTO> selected = !defaults.isEmpty() ? defaults : grupo.getIngredientes();
                for (IngredienteDTO ing : selected) {
                    totalDefaults += precioIngrediente(ing);
                }
            } else if (esOpcionalUnico) {
                // Si no hay default, el defaultTotal es 0 (grupo realmente opcional).
                IngredienteDTO def = firstDefault(grupo.getIngredientes());
                totalDefaults += precioIngrediente(def);
            } else {
                for (IngredienteDTO ing : grupo.getIngredientes()) {
                    if (ing != null && Boolean.TRUE.equals(ing.getSeleccionadoPorDefecto())) {
                        totalDefaults += precioIngrediente(ing);
                    }
                }
            }
        }

        int extras = totalSeleccionado - totalDefaults;
        if (extras < 0) {
            extras = 0;
        }
        return productoDTO.getPrecioBase() + extras;
    }

    public int getSubtotal() {
        return getPrecioUnitario() * quantity;
    }

    private static IngredienteDTO firstDefault(List<IngredienteDTO> ingredientes) {
        if (ingredientes == null) {
            return null;
        }
        for (IngredienteDTO ing : ingredientes) {
            if (ing != null && Boolean.TRUE.equals(ing.getSeleccionadoPorDefecto())) {
                return ing;
            }
        }
        return null;
    }

    private static List<IngredienteDTO> defaults(List<IngredienteDTO> ingredientes) {
        if (ingredientes == null) {
            return List.of();
        }
        List<IngredienteDTO> result = new ArrayList<>();
        for (IngredienteDTO ing : ingredientes) {
            if (ing != null && Boolean.TRUE.equals(ing.getSeleccionadoPorDefecto())) {
                result.add(ing);
            }
        }
        return result;
    }

    private static int precioIngrediente(IngredienteDTO ing) {
        if (ing == null) {
            return 0;
        }
        int unit = ing.getItemPrice() != null ? ing.getItemPrice() : 0;
        int cant = ing.getCantidad() != null ? ing.getCantidad() : 1;
        return unit * cant;
    }

    /**
     * Clave única para identificar este item en el carrito: productoId + ids de
     * ingredientes ordenados.
     */
    public String getCartKey() {
        if (ingredientesIds == null || ingredientesIds.isEmpty()) {
            return String.valueOf(productoDTO.getId());
        }

        List<Long> ids = ingredientesIds.stream()
                .filter(id -> id != null)
                .sorted()
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append(productoDTO.getId()).append("_");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sb.append("-");
            }
            sb.append(ids.get(i));
        }
        return sb.toString();
    }

    /**
     * Resumen de los ingredientes OBLIGATORIO (por unidad de producto), formateado
     * como: "Piezas de pollo x2, salsa picante x1".
     */
    public String getObligatoriosResumen() {
        if (productoDTO == null || productoDTO.getGruposIngredientes() == null || productoDTO.getGruposIngredientes().isEmpty()) {
            return "";
        }

        Map<String, Integer> cantidades = new LinkedHashMap<>();

        for (var grupo : productoDTO.getGruposIngredientes()) {
            if (grupo == null || grupo.getIngredientes() == null || grupo.getIngredientes().isEmpty()) {
                continue;
            }
            String tipo = grupo.getTipo() != null ? grupo.getTipo().trim().toUpperCase() : "";
            if (!"OBLIGATORIO".equals(tipo)) {
                continue;
            }

            List<IngredienteDTO> selected = new ArrayList<>();
            for (IngredienteDTO ing : grupo.getIngredientes()) {
                if (ing == null || ing.getId() == null) {
                    continue;
                }
                if (ingredientesIds != null && ingredientesIds.contains(ing.getId())) {
                    selected.add(ing);
                }
            }
            if (selected.isEmpty()) {
                selected = defaults(grupo.getIngredientes());
            }
            if (selected.isEmpty()) {
                selected = grupo.getIngredientes();
            }

            for (IngredienteDTO ing : selected) {
                if (ing == null) {
                    continue;
                }
                String name = ing.getItemName();
                if (name == null || name.isBlank()) {
                    continue;
                }
                int cant = ing.getCantidad() != null ? ing.getCantidad() : 1;
                cantidades.merge(name, cant, Integer::sum);
            }
        }

        if (cantidades.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> e : cantidades.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(e.getKey()).append(" x").append(e.getValue());
        }
        return sb.toString();
    }
}
