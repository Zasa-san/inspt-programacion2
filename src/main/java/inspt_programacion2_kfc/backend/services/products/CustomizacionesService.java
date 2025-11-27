package inspt_programacion2_kfc.backend.services.products;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.product.CustomizacionNotFoundException;
import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.repositories.products.CustomizacionesRepository;

@Service
public class CustomizacionesService {

    private final CustomizacionesRepository customizationRepository;

    public CustomizacionesService(CustomizacionesRepository customizationRepository) {
        this.customizationRepository = customizationRepository;
    }

    public List<CustomizacionEntity> findAll() {
        return customizationRepository.findAll();
    }

    public CustomizacionEntity findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID de customizacion invalido.");
        }
        return customizationRepository.findById(id).orElse(null);
    }

    public CustomizacionEntity create(CustomizacionEntity customizacion) {
        if (customizacion == null) {
            throw new IllegalArgumentException("La customizacion no puede ser nula.");
        }

        return customizationRepository.save(customizacion);
    }

    public CustomizacionEntity update(Long id, CustomizacionEntity updated) {
        if (id == null) {
            throw new IllegalArgumentException("ID de customizacion invalido.");
        }

        CustomizacionEntity existing = findById(id);

        if (existing == null) {
            throw new CustomizacionNotFoundException(String.format("Customizacion con id %s no encontrada.", id));
        }

        if (updated.getNombre() != null) {
            existing.setNombre(updated.getNombre());
        }

        existing.setPriceModifier(updated.getPriceModifier());

        if (updated.getProducto() != null) {
            existing.setProducto(updated.getProducto());
        }

        return customizationRepository.save(existing);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID de customizacion invalido.");
        }

        if (!customizationRepository.existsById(id)) {
            throw new CustomizacionNotFoundException(String.format("Customizacion con id %s no encontrada.", id));
        }

        customizationRepository.deleteById(id);
    }

    @Transactional
    public void deleteByProducto(ProductoEntity producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser nulo.");
        }
        customizationRepository.deleteByProducto(producto);
    }

    public List<CustomizacionEntity> findByProducto(ProductoEntity producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Producto no puede ser nulo.");
        }
        return customizationRepository.findByProducto(producto);
    }
}
