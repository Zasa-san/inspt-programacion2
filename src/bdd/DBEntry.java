package bdd;

public class DBEntry {

    private final String nombre;
    private final float precio;
    private final int idProducto;
    private int vendido;
    private int disponible;

    public DBEntry(String nombre, int disponible, float precio, int idProducto) {
        this.nombre = nombre;
        this.disponible = disponible;
        this.precio = precio;
        this.idProducto = idProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void actualizarStock(int egreso) {
        this.vendido += egreso;
        this.disponible -= egreso;
    }

}
