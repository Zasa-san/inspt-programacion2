package inspt_programacion2_kfc.backend.repositories.orders;

import inspt_programacion2_kfc.backend.models.pedidos.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsPedidoRepository extends JpaRepository<ItemPedido, Long> {

}


