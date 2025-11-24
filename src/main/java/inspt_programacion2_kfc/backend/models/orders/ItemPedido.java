package inspt_programacion2_kfc.backend.models.orders;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "items_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductoEntity producto;

    @Column(nullable = false)
    private int quantity;

    /**
     * Precio unitario al momento del pedido, en centavos.
     */
    @Column(nullable = false)
    private int unitPrice;

    /**
     * Subtotal de este ítem (unitPrice * quantity) en centavos.
     */
    @Column(nullable = false)
    private int subtotal;
}


