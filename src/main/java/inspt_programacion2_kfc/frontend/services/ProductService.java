package inspt_programacion2_kfc.frontend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.models.Producto;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductoService productoService;

    public ProductService(ProductoService productoService) {
        this.productoService = productoService;
    }

    public List<Producto> findAll() {
        return productoService.findAll()
                .stream()
                .map(p -> new Producto(p.getId(), p.getName(), p.getDescription(), p.getPrice()))
                .collect(Collectors.toList());
    }

    public Optional<Producto> findById(Long id) {
        return productoService.findById(id)
                .map(p -> new Producto(p.getId(), p.getName(), p.getDescription(), p.getPrice()));
    }
}


