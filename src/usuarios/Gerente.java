package usuarios;

import bdd.BaseDeProductos;
import bdd.DBEntry;
import items.Producto;

public class Gerente extends Usuario implements AdministrarStock {

    public Gerente(String nombre) {
        super(nombre);
    }

    @Override
    public void agregarAStock(Producto item, int cantidad) {
        BaseDeProductos.agregarProducto(new DBEntry(item.getNombre(), cantidad, item.getPrecio(), item.getId()));
    }

}
