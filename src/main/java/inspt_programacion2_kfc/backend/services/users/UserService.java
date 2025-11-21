package inspt_programacion2_kfc.backend.services.users;

import inspt_programacion2_kfc.backend.exceptions.user.*;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.repositories.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un usuario nuevo o actualiza la contraseña de uno existente.
     *
     * @param username      nombre de usuario (único)
     * @param rawPassword   contraseña en texto plano (se encripta internamente)
     * @param role          rol a asignar (enum {@link Role})
     * @param resetIfExists si true, resetea la contraseña si el usuario ya
     *                      existe
     * @throws UserCreationFailedException      si no se pudo crear el usuario
     * @throws UserPasswordResetFailedException si no se pudo resetear la
     *                                          contraseña
     */
    public void create(String username, String rawPassword, Role role, boolean resetIfExists) {
        if (resetIfExists) {
            User dbUser = findByUsername(username);
            dbUser.setPassword(passwordEncoder.encode(rawPassword));
            try {
                log.info("Clave reestablecida correctamente para el usuario {}", dbUser.getUsername());
                userRepository.save(dbUser);
                return;
            } catch (DataIntegrityViolationException ex) {
                throw new UserPasswordResetFailedException("Error al reestablecer la clave " + username, ex);
            }
        }

        if (existsByUsername(username)) throw new UserAlreadyExistsException(String.format("El usuario %s ya existe.", username));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(role);
        newUser.setEnabled(true);
        try {
            userRepository.save(newUser);
            log.info("Usuario {} guardado correctamente.", newUser.getUsername());
        } catch (DataIntegrityViolationException ex) {
            throw new UserCreationFailedException("Error al crear el usuario " + username, ex);
        }

    }

    /**
     * Devuelve todos los usuarios en la base.
     *
     * @return lista de usuarios
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Buscar un usuario por id.
     *
     * @param id id del usuario
     * @return usuario si existe
     */
    public User findById(Long id) {
        if (id == null) throw new UsernameNotFoundException("El id no puede ser nulo.");
        return userRepository.findById(id).orElse(null);
    }

    public boolean existsByUsername(String username) {
        if (username.isEmpty()) throw new UsernameNotFoundException("El nombre no puede ser nulo o vacio.");
        return userRepository.findByUsername(username).isPresent();
    }

    public User findByUsername(String username) {
        if (username.isEmpty()) throw new UsernameNotFoundException("El nombre no puede ser nulo o vacio.");
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Actualiza los datos de un usuario existente. Si la contraseña es null o
     * vacía, no se modifica.
     *
     * @param id          id del usuario a actualizar
     * @param username    nuevo username
     * @param rawPassword nueva contraseña en texto plano
     * @param role        nuevo rol
     */
    public void update(Long id, String username, String rawPassword, Role role) {
        User user = findById(id);

        user.setUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setRole(role);
        userRepository.save(user);
    }

    /**
     * Elimina un usuario por su id.
     *
     * @param id id del usuario a eliminar
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("Usuario con id %s no fue encontrado o no existe ", id));
        }
        userRepository.deleteById(id);
    }

    /**
     * Cambia el estado habilitado/deshabilitado de un usuario.
     *
     * @param id id del usuario
     * @param enabled nuevo estado
     */
    public void toggleEnabled(Long id, boolean enabled) {
        User user = findById(id);

        user.setEnabled(enabled);
        userRepository.save(user);
    }
}
