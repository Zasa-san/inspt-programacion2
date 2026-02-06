package inspt_programacion2_kfc.backend.repositories.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inspt_programacion2_kfc.backend.models.users.AsignacionTurno;

@Repository
public interface AsignacionTurnoRepository extends JpaRepository<AsignacionTurno, Long> {

    boolean existsByUsuario_IdAndTurno_IdAndVigenteTrue(Long userId, Long turnoId);

    @Query("""
            select a from AsignacionTurno a
            join fetch a.usuario u
            join fetch a.turno t
            where t.dia = :dia and a.vigente = true
            order by t.ingreso asc, u.apellido asc, u.nombre asc, u.username asc
            """)
    List<AsignacionTurno> findVigentesByDiaWithUsuarioAndTurno(@Param("dia") int dia);

    @Query("""
            select a from AsignacionTurno a
            join fetch a.usuario u
            join fetch a.turno t
            where t.id = :turnoId and a.vigente = true
            order by u.apellido asc, u.nombre asc, u.username asc
            """)
    List<AsignacionTurno> findVigentesByTurnoIdWithUsuarioAndTurno(@Param("turnoId") Long turnoId);
}
