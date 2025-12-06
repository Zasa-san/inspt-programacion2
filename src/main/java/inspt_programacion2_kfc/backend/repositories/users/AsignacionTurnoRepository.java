package inspt_programacion2_kfc.backend.repositories.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inspt_programacion2_kfc.backend.models.users.AsignacionTurno;

@Repository
public interface AsignacionTurnoRepository extends JpaRepository<AsignacionTurno, Long> {

}
