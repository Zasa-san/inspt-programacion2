package inspt_programacion2_kfc.frontend.models;

public class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final int price; // en centavos para evitar problemas de coma flotante

    public Product(Long id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }
}


