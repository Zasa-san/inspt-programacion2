package inspt_programacion2_kfc.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inspt_programacion2_kfc.backend.dto.users.UserRequestDTO;
import inspt_programacion2_kfc.backend.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.users.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO userRequest) {
        try {
            String role = userRequest.getRole() != null ? userRequest.getRole() : "ROLE_USER";

            User user = userService.create(
                    userRequest.getUsername(),
                    userRequest.getPassword(),
                    Role.valueOf(role),
                    false
            );

            UserResponseDTO response = new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().toString(),
                    user.isEnabled()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    public static class AuthResponseDTO {

        private final String username;
        private final String message;

        public AuthResponseDTO(String username, String message) {
            this.username = username;
            this.message = message;
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }
    }
}
