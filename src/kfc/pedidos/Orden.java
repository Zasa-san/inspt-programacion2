package kfc.pedidos;

public class Orden {

    private static int contador = 0;
    private final String cliente;
    private final int orden;

    /**
     * @param cliente a nombre de quien está el pedido
     */
    public Orden(String cliente) {
        this.cliente = cliente;
        this.orden = contador++;
    }

    @Override
    public String toString() {
        return "Orden #" + orden + " | Cliente: " + cliente;
    }
}
