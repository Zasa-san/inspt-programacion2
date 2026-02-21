package inspt_programacion2_kfc.backend.repositories.stock;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.stock.MovimientoStock;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

    List<MovimientoStock> findByItemId(Long itemId);

    List<MovimientoStock> findAllByOrderByFechaDesc();

    List<MovimientoStock> findAllByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);
}
