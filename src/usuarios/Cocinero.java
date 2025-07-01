package usuarios;

import bdd.BaseDeProductos;
import bdd.DBEntry;
import java.util.ArrayList;

public class Cocinero extends Usuario implements ConsultarStock {

    public Cocinero(String nombre) {
        super(nombre);
    }

    @Override
    public DBEntry consultarStock(int id) {
        return BaseDeProductos.getProductoPorId(id);
    }

    public void despachar(int idProducto) {
        DBEntry producto = consultarStock(idProducto);
        System.out.println("Despachando " + producto.getNombre() + "(id: " + producto.getIdProducto() + ")");
    }

    public void despachar(ArrayList<Integer> idProductos, String nombreDeCombo) {
        ArrayList<DBEntry> productos = new ArrayList();
        for (Integer idProducto : idProductos) {
            productos.add(consultarStock(idProducto));
        }
        System.out.println("Despachando combo " + nombreDeCombo + ",con los contenidos:");
        for (DBEntry producto : productos) {
            System.out.println(producto.getNombre() + "(id: " + producto.getIdProducto() + ")");
        }
    }

}
