package items;

public class Item {

    private static int idAutoincremental = 0;
    private final String nombre;
    private final Float precio;
    private final int id;

    /**
     * @param nombre nombre del item
     * @param precio precio del item
     */
    public Item(String nombre, Float precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.id = idAutoincremental++;
    }

    /**
     * @return precio del item
     */
    public Float getPrecio() {
        return this.precio;
    }

    @Override
    public String toString() {
        return "Item " + nombre + ", precio: $" + precio;
    }
}
