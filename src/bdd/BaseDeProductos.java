package bdd;

import java.util.ArrayList;

public class BaseDeProductos {

    private static ArrayList<DBEntry> base = new ArrayList();

    /**
     * @param producto objecto que representa una fila en la base de datos
     */
    public static void agregarProducto(DBEntry producto) {
        base.add(producto);
    }

    /**
     * @param id del producto que se vende
     * @param cantidadVendida cantidad que se vende
     */
    public static void registrarVenta(int id, int cantidadVendida) {
        for (dbEntry entry : base) {
            if (entry.getIdProducto() == id) {
                entry.actualizarStock(cantidadVendida);
            }
        }
    }

}
