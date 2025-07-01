package kfc;

import bdd.BaseDeIngredientes;
import bdd.BaseDeProductos;
import bdd.printer.DBPrinter;
import items.Combo;
import items.Ingrediente;
import items.Producto;
import items.productos.Hamburguesa;
import items.productos.Papas;
import usuarios.Gerente;
import usuarios.Vendedor;
import java.util.ArrayList;
import java.util.Arrays;
import usuarios.Cocinero;

public class KFC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //USUARIOS
        Gerente gerente1 = new Gerente("Romina Perez");
        Vendedor vendedor = new Vendedor("Jose Jovic");
        Cocinero cocinero = new Cocinero("Fernando Laprida");

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

        //EJEMPLO INICIALIZACION DE COMBOS
        Combo combo1 = new Combo("Chicken doble", new ArrayList<>(Arrays.asList(
                hamburguesaDoble.getId(),
                papasSimples.getId(),
                cocaCola.getId())));

        //ASIGNACIÓN DE STOCK POR USUARIO GERENTE
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

        //EJEMPLO DE VENTA REGISTRADA POR EL VENDEDOR EN CASO DE COMBO
        for (Integer idProducto : combo1.getContenidosPorId()) {
            vendedor.registrarVenta(idProducto, 1);
        }
        cocinero.despachar(combo1.getContenidosPorId(), combo1.getNombre());

        //CASO DE VENTA DE PRODUCTO UNICO
        System.out.println("");
        vendedor.registrarVenta(sprite.getId(), 2);
        cocinero.despachar(sprite.getId());

        System.out.println("");
        //MUESTREO DE VENTAS POR EL GERENTE
        gerente1.listarVentas();
        System.out.println("");

        //MUESTREO DE STOCK FINAL
        DBPrinter.print(BaseDeProductos.class.getSimpleName(), BaseDeProductos.getAllProductos());
        DBPrinter.print(BaseDeIngredientes.class.getSimpleName(), BaseDeIngredientes.getAllIngredientes());
    }
}
