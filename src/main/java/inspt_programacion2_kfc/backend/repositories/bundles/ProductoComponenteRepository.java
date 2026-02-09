package inspt_programacion2_kfc.backend.repositories.bundles;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.bundles.ProductoComponenteEntity;

public interface ProductoComponenteRepository extends JpaRepository<ProductoComponenteEntity, Long> {
    @EntityGraph(attributePaths = {"componente"})
    List<ProductoComponenteEntity> findByProductoId(Long productoId);

    void deleteByProductoId(Long productoId);
}

