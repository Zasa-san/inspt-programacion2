package items;

public class Producto {

    private static int idAutoincremental = 1;
    private final String nombre;
    private final int id;
    private Float precio;

    /**
     * @param nombre nombre del producto
     * @param precio precio del producto
     */
    public Producto(String nombre, Float precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.id = idAutoincremental++;
    }

    /**
     * @return precio del producto
     */
    public Float getPrecio() {
        return this.precio;
    }

    /**
     * @return id del producto
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return nomre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param variacion valor positivo o negativo que actualiza el precio
     */
    public void setPrecio(Float variacion) {
        this.precio += variacion;
    }

    @Override
    public String toString() {
        return "Producto " + nombre + ", precio: $" + precio;
    }
}
