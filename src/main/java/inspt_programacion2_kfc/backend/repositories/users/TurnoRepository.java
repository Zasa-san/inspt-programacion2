package inspt_programacion2_kfc.backend.repositories.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inspt_programacion2_kfc.backend.models.users.Turno;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findAllByOrderByDiaAscIngresoAsc();

    List<Turno> findByDiaOrderByIngresoAsc(int dia);
}
