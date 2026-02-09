package inspt_programacion2_kfc.backend.models.products;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ingredientes")
public class Ingredientes {

    //tipo : 
    // - ingrediente principal obligatorios
    // - adicional multiple
    // - adicional unico
}
