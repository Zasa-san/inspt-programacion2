package inspt_programacion2_kfc.backend.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;

public interface CustomizacionesRepository extends JpaRepository<CustomizacionEntity, Long> {

}
