package usuarios;

import items.Producto;

public interface AdministrarStock {

    /**
     * @param cantidad que se quiere agregar
     * @param item producto agregado al stock
     */
    public void agregarAStock(Producto item, int cantidad);
}
