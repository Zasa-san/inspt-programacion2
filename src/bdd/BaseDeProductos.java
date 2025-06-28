package bdd;

import java.util.ArrayList;
import java.util.List;

public class BaseDeProductos {

    private static final ArrayList<DBEntry> base = new ArrayList<>();

    public static void agregarProducto(DBEntry producto) {
        base.add(producto);
    }

    public static void registrarVenta(int id, int cantidadVendida) {
        for (DBEntry entry : base) {
            if (entry.getIdProducto() == id) {
                entry.actualizarStock(cantidadVendida);
                break;
            }
        }
    }

    public static List<DBEntry> getAllProductos() {
        return base;
    }
}
