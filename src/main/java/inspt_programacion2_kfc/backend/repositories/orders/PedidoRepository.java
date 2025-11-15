package inspt_programacion2_kfc.backend.repositories.orders;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.orders.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}


