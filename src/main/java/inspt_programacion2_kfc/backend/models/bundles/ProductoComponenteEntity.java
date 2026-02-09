package inspt_programacion2_kfc.backend.models.bundles;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
        name = "producto_componentes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "componente_id"})
)
public class ProductoComponenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Producto "bundle" (por ejemplo: Combo).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    /**
     * Producto componente (por ejemplo: Sánguche, Papas, Bebida).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "componente_id", nullable = false)
    private ProductoEntity componente;

    @Column(nullable = false)
    private int cantidad;
}

