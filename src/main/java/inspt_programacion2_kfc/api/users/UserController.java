package inspt_programacion2_kfc.api.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inspt_programacion2_kfc.backend.dto.users.UserRequestDTO;
import inspt_programacion2_kfc.backend.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.services.users.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crear un nuevo usuario.
     *
     * @param userRequest DTO con los datos del usuario
     * @return Respuesta con los datos del usuario creado
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequest) {
        try {
            var user = userService.create(
                    userRequest.getUsername(),
                    userRequest.getPassword(),
                    Role.valueOf(userRequest.getRole()),
                    false
            );

            UserResponseDTO response = new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().toString(),
                    user.isEnabled()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Obtener información del usuario actual autenticado.
     *
     * @return Respuesta con los datos del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        return ResponseEntity.ok("Información del usuario autenticado");
    }
}
