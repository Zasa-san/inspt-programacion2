package items;

import java.util.ArrayList;

public class Hamburguesa extends Item implements UsaIngredientes {

    private final ArrayList<Item> ingredientes;

    public Hamburguesa(String nombre, Float precio, ArrayList<Item> ingredientes) {
        super(nombre, precio);
        this.ingredientes = ingredientes;
    }

    @Override
    public void agregar(Item ingrediente) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void quitar(Item ingrediente) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
