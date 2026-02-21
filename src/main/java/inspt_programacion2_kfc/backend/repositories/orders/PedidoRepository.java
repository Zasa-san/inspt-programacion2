package inspt_programacion2_kfc.backend.repositories.orders;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.pedidos.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByCreatedAtDesc();

    List<Pedido> findAllByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime desde, LocalDateTime hasta);

}
