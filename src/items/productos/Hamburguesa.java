package items.productos;

import items.Item;
import items.UsaIngredientes;

import java.util.ArrayList;
import java.util.List;

public class Hamburguesa extends Item implements UsaIngredientes {

    private final List<Item> ingredientes;

    public Hamburguesa(String nombre, Float precio) {
        super(nombre, precio);
        this.ingredientes = new ArrayList<>(); //todo agregar ingredientes base
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
