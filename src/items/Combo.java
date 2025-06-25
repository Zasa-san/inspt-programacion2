package items;

import java.util.ArrayList;

public class Combo extends Item {
    private final ArrayList<Item> items;

    /**
     * @param nombre nombre del item
     * @param precio precio del item
     *
     */
    public Combo(String nombre, Float precio, ArrayList<Item> items) {
        super(nombre, precio);
        this.items = items;
    }
}
