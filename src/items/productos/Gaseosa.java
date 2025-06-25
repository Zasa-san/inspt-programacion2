package items.productos;

import items.Item;
import items.Size;

public class Gaseosa extends Item {

    private final Size size;

    /**
     * @param nombre nombre del producto
     * @param precio precio del producto
     * @param size tamaño del producto
     */
    public Gaseosa(String nombre, Float precio, Size size) {
        super(nombre, precio);
        this.size = size;
    }

}
