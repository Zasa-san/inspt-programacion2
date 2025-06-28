package usuarios;

public class Usuario {

    private static int idAutoincremental = 1;
    private final int idEmpleado;
    private final String nombre;

    /**
     * @param nombre nombre del usuario
     */
    public Usuario(String nombre) {
        this.idEmpleado = idAutoincremental++;
        this.nombre = nombre;
    }

}
