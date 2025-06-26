package bdd;

import items.Ingrediente;
import java.util.HashMap;
import java.util.Map;

public class BaseDeIngredientes {

    private static final Map<String, Ingrediente> ingredientes = new HashMap<>();

    static {
        inicializarIngredientesBase();
    }

    private static void inicializarIngredientesBase() {
        // Ingredientes base con claves predefinidas
        agregarIngrediente("PAN", new Ingrediente("Pan", 50F));
        agregarIngrediente("MEDALLON_POLLO", new Ingrediente("Medallón de pollo", 200F));
        agregarIngrediente("CHEDDAR", new Ingrediente("Cheddar", 100F));
        agregarIngrediente("LECHUGA", new Ingrediente("Lechuga", 30F));
        agregarIngrediente("TOMATE", new Ingrediente("Tomate", 40F));
        agregarIngrediente("BACON", new Ingrediente("Bacon", 140F));
        agregarIngrediente("PAPAS", new Ingrediente("Papas", 3000F));
    }

    public static void agregarIngrediente(String clave, Ingrediente ingrediente) {
        if (ingredientes.containsKey(clave)) {
            throw new IllegalArgumentException("La clave ya existe: " + clave);
        }
        ingredientes.put(clave, ingrediente);
    }

    public static Ingrediente getIngrediente(String clave) {
        Ingrediente ingrediente = ingredientes.get(clave);
        if (ingrediente == null) {
            throw new IllegalArgumentException("Ingrediente no encontrado para la clave: " + clave);
        }
        return ingrediente;
    }
}
