package inspt_programacion2_kfc.backend.models.products;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    /**
     * Precio en centavos (por ejemplo 5500 = $55,00)
     */
    @Column(nullable = false)
    private int price;

    @Column(nullable = true)
    private String imgUrl;

    @Column(nullable = false)
    private boolean available = true;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private Set<CustomizacionEntity> customizaciones = new HashSet<>();
}
