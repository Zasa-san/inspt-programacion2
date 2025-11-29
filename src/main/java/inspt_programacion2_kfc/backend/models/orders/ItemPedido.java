package inspt_programacion2_kfc.backend.models.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import jakarta.persistence.Transient;
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
    @JoinColumn(name = "producto_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

    /**
     * JSON con las customizaciones seleccionadas al momento del pedido.
     * Ejemplo: [{"id":1,"nombre":"Queso extra","precio":500}]
     * Puede ser null si no se eligieron customizaciones.
     */
    @Column(columnDefinition = "TEXT")
    private String customizacionesJson;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Devuelve los nombres de las customizaciones seleccionadas.
     * Útil para mostrar en la vista de pedidos.
     */
    @Transient
    public List<String> getCustomizacionesNombres() {
        if (customizacionesJson == null || customizacionesJson.isBlank()) {
            return List.of();
        }
        try {
            List<Map<String, Object>> customList = objectMapper.readValue(
                    customizacionesJson, 
                    new TypeReference<List<Map<String, Object>>>() {});
            List<String> nombres = new ArrayList<>();
            for (Map<String, Object> custom : customList) {
                Object nombre = custom.get("nombre");
                if (nombre != null) {
                    nombres.add(nombre.toString());
                }
            }
            return nombres;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Verifica si este item tiene customizaciones seleccionadas.
     */
    @Transient
    public boolean tieneCustomizaciones() {
        return customizacionesJson != null && !customizacionesJson.isBlank() 
                && !customizacionesJson.equals("[]");
    }
}
