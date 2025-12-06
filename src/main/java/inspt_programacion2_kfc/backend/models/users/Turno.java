package inspt_programacion2_kfc.backend.models.users;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Time ingreso;

    @Column(nullable = false)
    private Time salida;

    @Column(nullable = false)
    @Min(1)
    @Max(7)
    private int dia;

    @OneToMany(mappedBy = "turno")
    private List<AsignacionTurno> asignaciones = new ArrayList<>();

}
