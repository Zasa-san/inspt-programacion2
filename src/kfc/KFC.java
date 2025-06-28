package kfc;

import bdd.BaseDeIngredientes;
import bdd.BaseDeProductos;
import bdd.printer.DBPrinter;
import items.Ingrediente;
import items.Producto;
import items.productos.Hamburguesa;
import items.productos.Papas;
import usuarios.Gerente;

public class KFC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //USUARIOS
        Gerente gerente1 = new Gerente("Romina Perez");

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

        //ALGUNOS PRODUCTOS NO NECESITAN CLASE PROPIA
        Producto cocaCola = new Producto("Coca-Cola", 1500F);
        Producto sprite = new Producto("Sprite", 1500F);
        Producto patas = new Producto("Patas de pollo", 500F);
        Producto alitas = new Producto("Alitas de pollo", 450F);
        Producto pechuga = new Producto("Supremas de pollo", 2000F);

        //INCIALIZACIÓN DE STOCK
        gerente1.agregarAStock(hamburguesaSimple, 5000);
        gerente1.agregarAStock(hamburguesaDoble, 5000);
        gerente1.agregarAStock(hamburguesaBacon, 5000);
        gerente1.agregarAStock(papasSimples, 5000);
        gerente1.agregarAStock(papasConChedar, 5000);
        gerente1.agregarAStock(papasBravas, 5000);
        gerente1.agregarAStock(cocaCola, 5000);
        gerente1.agregarAStock(sprite, 5000);
        gerente1.agregarAStock(patas, 5000);
        gerente1.agregarAStock(alitas, 5000);
        gerente1.agregarAStock(pechuga, 5000);

        //MUESTREO DE STOCK EXISTENTE
        DBPrinter.print(BaseDeProductos.class.getSimpleName(), BaseDeProductos.getAllProductos());
        DBPrinter.print(BaseDeIngredientes.class.getSimpleName(), BaseDeIngredientes.getAllIngredientes());

        /*
        La idea es que el gerente sea quien agregar productos y puede pedir ventas de la base

        Por otro lado solo el vendedor puede registrarVentas en la base

        El cocinero no tiene responsabilidades
         */
    }
}
