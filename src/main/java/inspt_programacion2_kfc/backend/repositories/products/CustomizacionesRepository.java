package inspt_programacion2_kfc.backend.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;

public interface CustomizacionesRepository extends JpaRepository<CustomizacionEntity, Long> {
    List<CustomizacionEntity> findByProducto(ProductoEntity producto);
    void deleteByProducto(ProductoEntity producto);
}
