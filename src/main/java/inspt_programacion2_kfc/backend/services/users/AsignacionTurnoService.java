package inspt_programacion2_kfc.backend.services.users;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.users.AsignacionTurno;
import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.repositories.users.AsignacionTurnoRepository;

@Service
@Transactional(readOnly = true)
public class AsignacionTurnoService {

    private final AsignacionTurnoRepository asignacionTurnoRepository;

    public AsignacionTurnoService(AsignacionTurnoRepository asignacionTurnoRepository) {
        this.asignacionTurnoRepository = asignacionTurnoRepository;
    }

    @Transactional
    public AsignacionTurno asignarTurno(User usuario, Turno turno, Timestamp inicio, Timestamp fin, boolean vigente) {
        AsignacionTurno asignacion = new AsignacionTurno();
        asignacion.setUsuario(usuario);
        asignacion.setTurno(turno);
        asignacion.setInicio(inicio);
        asignacion.setFin(fin);
        asignacion.setVigente(vigente);
        return asignacionTurnoRepository.save(asignacion);
    }

    @Transactional
    public AsignacionTurno asignarTurno(User usuario, Turno turno, Timestamp inicio, boolean vigente) {
        AsignacionTurno asignacion = new AsignacionTurno();
        asignacion.setUsuario(usuario);
        asignacion.setTurno(turno);
        asignacion.setInicio(inicio);
        asignacion.setVigente(vigente);
        return asignacionTurnoRepository.save(asignacion);
    }

}
