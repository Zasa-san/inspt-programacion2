package items;

import java.util.ArrayList;

public class Combo extends Producto {

    private final ArrayList<Producto> items;

    /**
     * @param nombre nombre del producto
     * @param precio precio del producto
     * @param items proudctos contenidos en el combo
     */
    public Combo(String nombre, Float precio, ArrayList<Producto> items) {
        super(nombre, precio);
        this.items = items;
    }
}
