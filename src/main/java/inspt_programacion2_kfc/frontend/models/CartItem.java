package inspt_programacion2_kfc.frontend.models;

import lombok.Data;

@Data
public class CartItem {

    private final Producto producto;
    private int quantity;

    public CartItem(Producto producto, int quantity) {
        this.producto = producto;
        this.quantity = quantity;
    }

    public void increment(int amount) {
        this.quantity += amount;
        if (this.quantity < 1) {
            this.quantity = 1;
        }
    }

    public int getSubtotal() {
        return producto.getPrice() * quantity;
    }
}


