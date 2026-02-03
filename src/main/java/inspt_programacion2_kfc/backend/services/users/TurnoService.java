package inspt_programacion2_kfc.backend.services.users;

import java.sql.Time;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.repositories.users.TurnoRepository;

@Service
@Transactional(readOnly = true)
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Transactional
    public Turno create(Time ingreso, Time salida, int dia) {
        Turno turno = new Turno();
        turno.setIngreso(ingreso);
        turno.setSalida(salida);
        turno.setDia(dia);
        return turnoRepository.save(turno);
    }

    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    public List<Turno> findAllSorted() {
        return turnoRepository.findAllByOrderByDiaAscIngresoAsc();
    }
}
