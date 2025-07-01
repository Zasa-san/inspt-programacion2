package bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, Integer> listarVentas() {
        Map<String, Integer> resultado = new HashMap<>();
        BaseDeProductos.getAllProductos().forEach(producto -> {
            if (producto.getVendido() > 0) {
                resultado.put(producto.getNombre(), producto.getVendido());
            }
        });
        return resultado;
    }
}
