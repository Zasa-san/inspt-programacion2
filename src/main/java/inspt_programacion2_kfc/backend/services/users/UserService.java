package inspt_programacion2_kfc.backend.services.users;

import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.repositories.users.UserRepository;
import inspt_programacion2_kfc.backend.services.users.exceptions.UserCreationFailedException;
import inspt_programacion2_kfc.backend.services.users.exceptions.UserPasswordResetFailedException;

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
     * @throws UserCreationFailedException si no se pudo crear el usuario
     * @throws UserPasswordResetFailedException si no se pudo resetear la
     * contraseña
     * @param username nombre de usuario (único)
     * @param rawPassword contraseña en texto plano (se encripta internamente)
     * @param role rol a asignar (enum {@link Role})
     * @param resetIfExists si true, resetea la contraseña si el usuario ya
     * existe
     * @return el usuario creado o existente (con contraseña actualizada si se
     * solicitó)
     */
    public User create(String username, String rawPassword, Role role, boolean resetIfExists) {
        Optional<User> dbUser = userRepository.findByUsername(username);
        if (dbUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setRole(role);
            newUser.setEnabled(true);
            try {
                return userRepository.save(newUser);
            } catch (DataIntegrityViolationException ex) {
                throw new UserCreationFailedException("Could not create user " + username, ex);
            }
        } else if (resetIfExists) {
            User existing = dbUser.get();
            existing.setPassword(passwordEncoder.encode(rawPassword));
            try {
                return userRepository.save(existing);
            } catch (DataIntegrityViolationException ex) {

                throw new UserPasswordResetFailedException("Could not reset password for user " + username, ex);
            }
        }
        return dbUser.get();
    }

    /**
     * Devuelve todos los usuarios en la base.
     *
     * @return lista de usuarios
     */
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Buscar un usuario por id.
     *
     * @param id id del usuario
     * @return usuario si existe
     */
    public java.util.Optional<User> findById(Long id) {
        Objects.requireNonNull(id, "ID no puede ser NULL");
        return userRepository.findById(id);
    }

    /**
     * Actualiza los datos de un usuario existente. Si la contraseña es null o
     * vacía, no se modifica.
     *
     * @param id id del usuario a actualizar
     * @param username nuevo username
     * @param rawPassword nueva contraseña en texto plano
     * @param role nuevo rol
     * @return usuario actualizado
     */
    public User update(Long id, String username, String rawPassword, Role role) {
        Objects.requireNonNull(id, "ID no puede ser NULL");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        user.setUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * Elimina un usuario por su id.
     *
     * @param id id del usuario a eliminar
     */
    public void delete(Long id) {
        Objects.requireNonNull(id, "ID no puede ser NULL");
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Cambia el estado habilitado/deshabilitado de un usuario.
     *
     * @param id id del usuario
     * @param enabled nuevo estado
     * @return usuario actualizado
     */
    public User toggleEnabled(Long id, boolean enabled) {
        Objects.requireNonNull(id, "ID no puede ser NULL");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        user.setEnabled(enabled);
        return userRepository.save(user);
    }
}
