package items.productos;

import items.Item;
import items.Size;
import items.UsaIngredientes;

import java.util.ArrayList;

public class Papas extends Item implements UsaIngredientes {

    private final Size size;
    private final ArrayList<Item> ingredientes;

    /**
     * @param nombre nombre del item
     * @param precio precio del item
     * @param size tamaño del item
     */
    public Papas(String nombre, Float precio, Size size, ArrayList<Item> ingredientes) {
        super(nombre, precio);
        this.size = size;
        this.ingredientes = ingredientes;
    }

    @Override
    public void agregar(Item ingrediente) {

    }

    @Override
    public void quitar(Item ingrediente) {

    }

    @Override
    public void crear(ArrayList<Item> ingredientes) {

    }
}
