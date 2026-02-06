package inspt_programacion2_kfc.backend.services.users;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

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

    public List<AsignacionTurno> findVigentesByDia(int dia) {
        return asignacionTurnoRepository.findVigentesByDiaWithUsuarioAndTurno(dia);
    }

    public List<AsignacionTurno> findVigentesByTurnoId(Long turnoId) {
        return asignacionTurnoRepository.findVigentesByTurnoIdWithUsuarioAndTurno(turnoId);
    }

    @Transactional
    public AsignacionTurno asignarTurnoVigenteUnicoPorDia(User usuario, Turno turno) {
        Objects.requireNonNull(usuario, "usuario no puede ser null");
        Objects.requireNonNull(turno, "turno no puede ser null");

        boolean alreadyAssignedInDay = asignacionTurnoRepository
                .existsByUsuario_IdAndTurno_DiaAndVigenteTrue(usuario.getId(), turno.getDia());
        if (alreadyAssignedInDay) {
            throw new IllegalStateException("El usuario ya tiene un turno asignado para ese día.");
        }

        AsignacionTurno asignacion = new AsignacionTurno();
        asignacion.setUsuario(usuario);
        asignacion.setTurno(turno);
        asignacion.setInicio(Timestamp.from(Instant.now()));
        asignacion.setVigente(true);
        return asignacionTurnoRepository.save(asignacion);
    }

    @Transactional
    public void eliminar(Long asignacionId) {
        asignacionTurnoRepository.deleteById(asignacionId);
    }

}
