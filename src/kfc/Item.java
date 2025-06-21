package kfc;

public class Item {

    private final String nombre;
    private final Float precio;

    /**
     * @param nombre nombre del item
     * @param precio precio del item
     */
    public Item(String nombre, Float precio) {
        this.nombre = nombre;
        this.precio = precio;
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
