package items.productos;

import bdd.BaseDeIngredientes;
import items.Ingrediente;
import items.Producto;

import items.UsaIngredientes;
import java.util.ArrayList;
import java.util.Arrays;

public class Papas extends Producto implements UsaIngredientes {

    private final ArrayList<Ingrediente> ingredientes;

    public Papas(String nombre, Float precio) {
        super(nombre, precio);
        this.ingredientes = new ArrayList<>(Arrays.asList(BaseDeIngredientes.getIngrediente("PAPAS")));
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
