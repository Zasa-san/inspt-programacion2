package inspt_programacion2_kfc.backend.repositories.products;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.products.Ingrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findByGrupoId(Long grupoId);

    List<Ingrediente> findByItemId(Long itemId);

}
