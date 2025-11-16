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

    public List<ProductoEntity> findAllAvailable() {
        return productoRepository.findByAvailableTrue();
    }

    public Optional<ProductoEntity> findById(Long id) {
        return productoRepository.findById(id);
    }

    public ProductoEntity save(ProductoEntity producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    public ProductoEntity update(ProductoEntity producto) {
        return productoRepository.save(producto);
    }

    public void toggleAvailability(Long id) {
        Optional<ProductoEntity> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            ProductoEntity p = producto.get();
            p.setAvailable(!p.isAvailable());
            productoRepository.save(p);
        }
    }
}
