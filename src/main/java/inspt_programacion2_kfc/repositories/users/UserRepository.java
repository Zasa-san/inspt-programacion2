package inspt_programacion2_kfc.repositories.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.models.users.Role;
import inspt_programacion2_kfc.models.users.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);
}
