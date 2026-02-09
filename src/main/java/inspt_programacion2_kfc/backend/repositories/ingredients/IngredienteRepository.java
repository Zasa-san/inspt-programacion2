package inspt_programacion2_kfc.backend.repositories.ingredients;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.ingredients.IngredienteEntity;

public interface IngredienteRepository extends JpaRepository<IngredienteEntity, Long> {
    Optional<IngredienteEntity> findByNameIgnoreCase(String name);
}

