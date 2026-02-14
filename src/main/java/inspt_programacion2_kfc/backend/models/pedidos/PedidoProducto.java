package inspt_programacion2_kfc.backend.models.pedidos;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "pedido_productos")
public class PedidoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Ingrediente ingrediente;

    @Column(nullable = false)
    private int cantidad = 1;

}
