package inspt_programacion2_kfc.backend.models.products;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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

    @Column
    private String imgUrl;

    @Column(nullable = false)
    private boolean available = true;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomizacionEntity> customizaciones = new ArrayList<>();
}

// los productos dejan de tener precio. porque el precio pasa a ser la sumatoria de ingredientes
// el precio pasa a ser una propiedad del pedido.
// el producto puede mostrar el precio base, pero serán los adicionales que finalmente determinan
// el precio del pedido
//
// al borrar un producto -> transacional con borrar todos los ingredientes
// al editar un procutos -> transacional con borrar los ingredientes quitados
