package inspt_programacion2_kfc.frontend.models;

public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increment(int amount) {
        this.quantity += amount;
        if (this.quantity < 1) {
            this.quantity = 1;
        }
    }

    public int getSubtotal() {
        return product.getPrice() * quantity;
    }
}


