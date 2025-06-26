package items;

public class Ingrediente {

    private final String nombre;
    private final Float precio;

    public Ingrediente(String nombre, Float precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public Float getPrecio() {
        return precio;
    }
}
