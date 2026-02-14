package inspt_programacion2_kfc.backend.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;

public interface GrupoIngredienteRepository extends JpaRepository<GrupoIngrediente, Long> {

    List<GrupoIngrediente> findByProductoId(Long productoId);

}
