package inspt_programacion2_kfc.backend.services.products;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.backend.exceptions.product.CustomizacionNotFoundException;
import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
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

        Optional<CustomizacionEntity> existingOpt = customizationRepository.findById(id);

        if (existingOpt.isEmpty()) {
            throw new CustomizacionNotFoundException(String.format("Customizacion con id %s no encontrada.", id));
        }

        CustomizacionEntity existing = existingOpt.get();

        if (updated.getSize() != null) {
            existing.setSize(updated.getSize());
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
}
