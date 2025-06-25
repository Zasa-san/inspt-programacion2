package kfc;

import items.productos.Bacon;

public class KFC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Bacon bacon = new Bacon();
        /*
        crear factory
        BaseDeDatos bdd
        insertar a bdd con nombre, precio, ingredientes, y cantidad por un metodo agregar (id autoincremental)
        pedidos trabaja con la bdd y nunca con instancias directamente
         */
//        Hamburguesa hamburguesa = new Hamburguesa("Bacon", 3000F, Arrays.asList(
//                new Item("Pan", 1000F),
//                new Item("Bacon", 2000F)));
//        Combo combo = new Combo("Combo 1", 10000, Arrays.asList(hamburguesa));
    }
}
