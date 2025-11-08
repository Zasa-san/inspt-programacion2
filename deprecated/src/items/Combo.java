package items;

import java.util.ArrayList;

public class Combo {

    private ArrayList<Integer> contenidosPorId;
    private String nombre;

    public Combo(String nombre, ArrayList<Integer> contenidosPorId) {
        this.contenidosPorId = contenidosPorId;
        this.nombre = nombre;
    }

    public ArrayList<Integer> getContenidosPorId() {
        return contenidosPorId;
    }

    public String getNombre() {
        return nombre;
    }
}
