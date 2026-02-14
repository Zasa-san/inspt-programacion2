package inspt_programacion2_kfc.backend.repositories.stock;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.stock.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}
