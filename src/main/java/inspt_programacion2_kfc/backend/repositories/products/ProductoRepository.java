package inspt_programacion2_kfc.backend.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    List<ProductoEntity> findByAvailableTrue();

}
