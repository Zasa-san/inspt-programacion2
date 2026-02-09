package inspt_programacion2_kfc.backend.repositories.ingredients;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import inspt_programacion2_kfc.backend.models.ingredients.ProductoIngredienteEntity;

public interface ProductoIngredienteRepository extends JpaRepository<ProductoIngredienteEntity, Long> {
    @EntityGraph(attributePaths = {"ingrediente"})
    List<ProductoIngredienteEntity> findByProductoId(Long productoId);

    boolean existsByProductoIdAndIngredienteId(Long productoId, Long ingredienteId);

    void deleteByProductoId(Long productoId);
}
