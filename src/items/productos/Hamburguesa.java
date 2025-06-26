package items.productos;

import bdd.BaseDeIngredientes;
import items.Ingrediente;
import items.Producto;
import items.UsaIngredientes;
import java.util.ArrayList;
import java.util.Arrays;

public class Hamburguesa extends Producto implements UsaIngredientes {

    private final ArrayList<Ingrediente> ingredientes;

    /**
     * @param nombre nombre del producto
     * @param precio precio básico del producto
     */
    public Hamburguesa(String nombre, Float precio) {
        super(nombre, precio);
        this.ingredientes = new ArrayList<>(Arrays.asList(BaseDeIngredientes.getIngrediente("PAN"), BaseDeIngredientes.getIngrediente("MEDALLON_POLLO"), BaseDeIngredientes.getIngrediente("CHEDDAR"), BaseDeIngredientes.getIngrediente("LECHUGA"), BaseDeIngredientes.getIngrediente("TOMATE")));
    }

    @Override
    public void agregar(Ingrediente ingrediente) {
        super.setPrecio(ingrediente.getPrecio());
        ingredientes.add(ingrediente);
    }

    @Override
    public void quitar(Ingrediente ingrediente) {
        ingredientes.remove(ingrediente);
    }
}
