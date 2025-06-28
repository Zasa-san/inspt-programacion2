package usuarios;

import bdd.BaseDeProductos;

public class Vendedor extends Usuario implements RegistrarVenta {

    public Vendedor(String nombre) {
        super(nombre);
    }

    @Override
    public void registrarVenta(int id, int cantidadVendida) {
        BaseDeProductos.registrarVenta(id, cantidadVendida);
    }

}
