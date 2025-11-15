package inspt_programacion2_kfc.backend.services.products;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoEntity> findAll() {
        return productoRepository.findAll();
    }

    public Optional<ProductoEntity> findById(Long id) {
        return productoRepository.findById(id);
    }

    public ProductoEntity save(ProductoEntity producto) {
        return productoRepository.save(producto);
    }
}


