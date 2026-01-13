package inspt_programacion2_kfc.backend.services.users;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.user.UserAlreadyExistsException;
import inspt_programacion2_kfc.backend.exceptions.user.UserCreationFailedException;
import inspt_programacion2_kfc.backend.exceptions.user.UserException;
import inspt_programacion2_kfc.backend.exceptions.user.UserNotFoundException;
import inspt_programacion2_kfc.backend.exceptions.user.UserPasswordResetFailedException;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.repositories.users.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un usuario nuevo con todos sus datos.
     *
     * @param username nombre de usuario (único)
     * @param rawPassword contraseña en texto plano (se encripta internamente)
     * @param dni documento de identidad (único)
     * @param nombre nombre del usuario
     * @param apellido apellido del usuario
     * @param role rol a asignar (enum {@link Role})
     * @param resetIfExists si true, resetea la contraseña si el usuario ya
     * existe
     * @return el usuario creado
     * @throws UserCreationFailedException si no se pudo crear el usuario
     * @throws UserPasswordResetFailedException si no se pudo resetear la
     * contraseña
     */
    @Transactional
    public User create(String username, String rawPassword, int dni, String nombre, String apellido, Role role, boolean resetIfExists) {
        boolean usernameExists = existsByUsername(username);
        boolean dniExists = existsByDni(dni);

        if ((usernameExists || dniExists) && resetIfExists) {
            User dbUser = findByUsername(username);
            if (dbUser != null) {
                dbUser.setPassword(passwordEncoder.encode(rawPassword));
                try {
                    User savedUser = userRepository.save(dbUser);
                    System.out.printf("Usuario %s actualizado correctamente.%n", dbUser.getUsername());
                    return savedUser;
                } catch (DataIntegrityViolationException ex) {
                    throw new UserPasswordResetFailedException("Error al actualizar el usuario " + username, ex);
                }
            }
        } else if (usernameExists) {
            throw new UserAlreadyExistsException(String.format("El nombre de usuario %s ya existe.", username));
        } else if (dniExists) {
            throw new UserAlreadyExistsException(String.format("El DNI %d ya está asignado a otro usuario.", dni));
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setDni(dni);
        newUser.setNombre(nombre);
        newUser.setApellido(apellido);
        newUser.setRole(role);
        newUser.setEnabled(true);

        try {
            User savedUser = userRepository.save(newUser);
            System.out.printf("Usuario %s guardado correctamente.%n", newUser.getUsername());
            return savedUser;
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
        if (id == null) {
            throw new UserException("El id no puede ser nulo.");
        }
        return userRepository.findById(id).orElse(null);
    }

    public boolean existsByUsername(String username) {
        if (username.isEmpty()) {
            throw new UsernameNotFoundException("El nombre no puede ser nulo o vacio.");
        }
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByDni(int dni) {
        return userRepository.findByDni(dni).isPresent();
    }

    public User findByUsername(String username) {
        if (username.isEmpty()) {
            throw new UsernameNotFoundException("El nombre no puede ser nulo o vacio.");
        }
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Actualiza los datos de un usuario existente
     *
     * @param id id del usuario a actualizar
     * @param username nuevo username
     * @param dni nuevo DNI
     * @param nombre nuevo nombre
     * @param apellido nuevo apellido
     * @param role nuevo rol
     * @param enabled estado habilitado/deshabilitado
     */
    @Transactional
    public void update(Long id, String username, int dni, String nombre, String apellido, Role role, boolean enabled) {
        User user = findById(id);

        user.setUsername(username);
        user.setDni(dni);
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setRole(role);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    /**
     * Elimina un usuario por su id.
     *
     * @param id id del usuario a eliminar
     */
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new UserException("El id no puede ser nulo.");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("Usuario con id %s no existe ", id));
        }
        userRepository.deleteById(id);
    }

    /**
     * Cambia el estado habilitado/deshabilitado de un usuario.
     *
     * @param id id del usuario
     * @param enabled nuevo estado
     */
    @Transactional
    public void toggleEnabled(Long id, boolean enabled) {
        User user = findById(id);
        if (user != null) {
            user.setEnabled(enabled);
            userRepository.save(user);
        }
    }

    /**
     * Cambia la contraseña de un usuario después de verificar la contraseña
     * actual.
     *
     * @param userId id del usuario
     * @param currentPassword contraseña actual en texto plano
     * @param newPassword nueva contraseña en texto plano
     * @return true si se cambió exitosamente, false si la contraseña actual es
     * incorrecta
     */
    @Transactional
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Cambiar la contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Verifica si la contraseña proporcionada es correcta para el usuario.
     *
     * @param userId id del usuario
     * @param password contraseña en texto plano
     * @return true si la contraseña es correcta
     */
    public boolean verifyPassword(Long userId, String password) {
        User user = findById(userId);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Cambia la contraseña de un usuario por un admin.
     *
     * @param userId id del usuario
     * @param newPassword nueva contraseña en texto plano
     */
    @Transactional
    public void changePasswordByAdmin(Long userId, String newPassword) {
        User user = findById(userId);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
