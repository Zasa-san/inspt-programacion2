package inspt_programacion2_kfc.backend.services.products;

import java.util.List;
import java.util.Objects;
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

    public List<ProductoEntity> findAllAvailable() {
        return productoRepository.findByAvailableTrue();
    }

    public ProductoEntity findById(Long id) {
        Optional<ProductoEntity> producto = productoRepository.findById(id);
        return producto.orElse(null);
    }

    public void save(ProductoEntity producto) {
        productoRepository.save(Objects.requireNonNull(producto));
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(Objects.requireNonNull(id));
    }

    public void update(ProductoEntity producto) {
        productoRepository.save(Objects.requireNonNull(producto));
    }

    public void toggleAvailability(Long id) {
        Optional<ProductoEntity> producto = productoRepository.findById(Objects.requireNonNull(id));
        if (producto.isPresent()) {
            ProductoEntity p = producto.get();
            p.setAvailable(!p.isAvailable());
            productoRepository.save(p);
        }
    }
}
