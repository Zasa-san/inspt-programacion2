package usuarios;

import bdd.BaseDeProductos;
import bdd.DBEntry;
import items.Producto;
import java.util.Map;

public class Gerente extends Usuario implements AdministrarStock {

    public Gerente(String nombre) {
        super(nombre);
    }

    @Override
    public void agregarAStock(Producto item, int cantidad) {
        BaseDeProductos.agregarProducto(new DBEntry(item.getNombre(), cantidad, item.getPrecio(), item.getId()));
    }

    public void listarVentas() {
        Map<String, Integer> resultados;
        resultados = BaseDeProductos.listarVentas();
        System.out.println("VENTAS:");
        resultados.forEach((nombre, cantidad) -> {
            System.out.println("- " + nombre + ". Cantidad vendida: " + cantidad);
        });
    }

}
