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
                return;
            }
        }
        System.out.println("El producto con id " + id + " no existe en la base.");
    }

    public static List<DBEntry> getAllProductos() {
        return base;
    }

    public static DBEntry getProductoPorId(Integer idProducto) {
        for (DBEntry producto : base) {
            if (idProducto.equals(producto.getIdProducto())) {
                return producto;
            }
        }
        System.out.println("Producto no encontrado.\n");
        return null;
    }
}
