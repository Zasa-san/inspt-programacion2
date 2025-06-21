package kfc.pedidos;

import java.util.ArrayList;
import kfc.Item;

enum Estado {
    CREADO,
    PREPARACION,
    ENTREGADO,
}

public class Pedido {

    private final ArrayList<Item> contenidos;
    private Estado estado;
    private final Orden orden;

    /**
     * @param contenidos items del pedido
     * @param cliente nombre del cliente
     */
    public Pedido(ArrayList<Item> contenidos, String cliente) {
        this.contenidos = contenidos;
        this.estado = Estado.CREADO;
        this.orden = new Orden(cliente);
    }

    /**
     * @param item agrega item al pedido
     */
    public void agregarItem(Item item) {
        this.contenidos.add(item);
    }

    /**
     * @param item quita item al pedido
     */
    public void quitarItem(Item item) {
        this.contenidos.remove(item);
    }

    /**
     * @return estado del pedido
     */
    public Estado getEstado() {
        return this.estado;
    }

    /**
     * marca el pedido como pagado y en preparación
     */
    public void pagar() {
        this.estado = Estado.PREPARACION;
    }

    /**
     * marca el pedido como entregado al cliente
     */
    public void entregar() {
        this.estado = Estado.ENTREGADO;
    }

    /**
     * @return precio del pedido
     */
    public Float getPrecio() {
        float precio = 0;
        for (Item item : contenidos) {
            precio += item.getPrecio();
        }
        return precio;
    }

    @Override
    public String toString() {
        return orden.toString() + "| Precio $" + this.getPrecio() + "| Estado " + this.getEstado();
    }

}
