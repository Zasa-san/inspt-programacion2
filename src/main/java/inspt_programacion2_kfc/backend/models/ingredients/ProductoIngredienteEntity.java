package inspt_programacion2_kfc.backend.models.ingredients;

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
        name = "producto_ingredientes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "ingrediente_id"})
)
public class ProductoIngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private IngredienteEntity ingrediente;

    /**
     * Cantidad de ingrediente necesaria para producir 1 unidad de producto.
     */
    @Column(nullable = false)
    private int cantidad;
}

