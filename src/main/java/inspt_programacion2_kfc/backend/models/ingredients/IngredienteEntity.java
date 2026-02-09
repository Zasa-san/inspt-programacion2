package inspt_programacion2_kfc.backend.models.ingredients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ingredientes")
public class IngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Unidad de medida visible (ej: "u", "g", "ml", "porción").
     * El stock se maneja como entero por simplicidad.
     */
    @Column(nullable = false)
    private String unit = "u";

    @Column(nullable = false)
    private boolean active = true;
}

