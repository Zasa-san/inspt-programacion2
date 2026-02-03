package inspt_programacion2_kfc.frontend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;

@Data
@AllArgsConstructor
public class TurnoDTO {

    private Long id;
    private Time ingreso;
    private Time salida;
    private String dia;

}
