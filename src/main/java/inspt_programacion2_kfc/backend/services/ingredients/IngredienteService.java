package inspt_programacion2_kfc.backend.services.ingredients;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.ingredients.IngredienteEntity;
import inspt_programacion2_kfc.backend.repositories.ingredients.IngredienteRepository;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<IngredienteEntity> findAllActive() {
        return ingredienteRepository.findAll().stream().filter(IngredienteEntity::isActive).toList();
    }

    public List<IngredienteEntity> findAll() {
        return ingredienteRepository.findAll();
    }

    public IngredienteEntity findById(Long id) {
        if (id == null) {
            return null;
        }
        return ingredienteRepository.findById(id).orElse(null);
    }

    @Transactional
    public IngredienteEntity create(IngredienteEntity ingrediente) {
        if (ingrediente == null || ingrediente.getName() == null) {
            return null;
        }
        IngredienteEntity existing = ingredienteRepository.findByNameIgnoreCase(ingrediente.getName()).orElse(null);
        if (existing != null) {
            if (ingrediente.getUnit() != null) {
                existing.setUnit(ingrediente.getUnit());
            }
            existing.setActive(ingrediente.isActive());
            return ingredienteRepository.save(existing);
        }
        return ingredienteRepository.save(ingrediente);
    }
}
