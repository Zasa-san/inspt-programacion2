package inspt_programacion2_kfc.frontend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.frontend.models.Product;

@Service
public class ProductService {

    // Por ahora, productos en memoria solo para la UI de cliente
    private final List<Product> products = List.of(
            new Product(1L, "Combo Clásico", "Sandwich de pollo frito + papas medianas + bebida.", 5500),
            new Product(2L, "Bucket Familiar", "8 piezas de pollo + 2 papas grandes + 4 bebidas.", 12900),
            new Product(3L, "Tenders Box", "6 tenders + papas chicas + bebida.", 6200),
            new Product(4L, "Helado Sundae", "Postre helado con salsa a elección.", 2500)
    );

    public List<Product> findAll() {
        return products;
    }

    public Optional<Product> findById(Long id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }
}


