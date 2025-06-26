package kfc;

import bdd.BaseDeIngredientes;
import bdd.BaseDeProductos;
import bdd.DBEntry;
import items.Ingrediente;
import items.productos.Hamburguesa;
import items.productos.Papas;

public class KFC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //INICIALIZACIÓN DE PRODUCTOS
        Hamburguesa hamburguesaSimple = new Hamburguesa("Hamburguesa Simple", 4350.76F);
        Hamburguesa hamburguesaDoble = new Hamburguesa("Hamburguesa Doble", 4350.76F);
        hamburguesaDoble.agregar(BaseDeIngredientes.getIngrediente("MEDALLON_POLLO"));
        Hamburguesa hamburguesaBacon = new Hamburguesa("Hamburguesa con Bacon", 4350.76F);
        hamburguesaBacon.agregar(BaseDeIngredientes.getIngrediente("BACON"));

        Papas papasSimples = new Papas("Papas fritas", 3000F);
        Papas papasConChedar = new Papas("Papas con chedar", 3000F);
        papasConChedar.agregar(BaseDeIngredientes.getIngrediente("CHEDDAR"));

        //ILUSTRATIVO DE CÓMO AGREGAR UN NUEVO INGREDIENTE
        BaseDeIngredientes.agregarIngrediente("SALSA_PICANTE", new Ingrediente("Salsa picante", 250F));

        Papas papasBravas = new Papas("Papas bravas", 3000F);
        papasBravas.agregar(BaseDeIngredientes.getIngrediente("SALSA_PICANTE"));

        //INCIALIZACIÓN DE STOCK
        BaseDeProductos.agregarProducto(new DBEntry(hamburguesaSimple.getNombre(), 5000, hamburguesaSimple.getPrecio(), hamburguesaSimple.getId()));
        BaseDeProductos.agregarProducto(new DBEntry(hamburguesaDoble.getNombre(), 5000, hamburguesaDoble.getPrecio(), hamburguesaDoble.getId()));
        BaseDeProductos.agregarProducto(new DBEntry(hamburguesaBacon.getNombre(), 5000, hamburguesaBacon.getPrecio(), hamburguesaBacon.getId()));
        BaseDeProductos.agregarProducto(new DBEntry(papasSimples.getNombre(), 5000, papasSimples.getPrecio(), papasSimples.getId()));
        BaseDeProductos.agregarProducto(new DBEntry(papasConChedar.getNombre(), 5000, papasConChedar.getPrecio(), papasConChedar.getId()));
        BaseDeProductos.agregarProducto(new DBEntry(papasBravas.getNombre(), 5000, papasBravas.getPrecio(), papasBravas.getId()));

    }
}
