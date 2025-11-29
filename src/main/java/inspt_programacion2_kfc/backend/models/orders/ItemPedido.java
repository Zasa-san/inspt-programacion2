package inspt_programacion2_kfc.backend.models.orders;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
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
@Table(name = "items_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

    @Column(columnDefinition = "TEXT")
    private String customizacionesJson;
}
