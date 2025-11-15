package inspt_programacion2_kfc.backend.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

}


