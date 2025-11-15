package inspt_programacion2_kfc.backend.models.dto.users;

import lombok.Data;

/**
 * DTO para las respuestas de usuarios (no expone la contraseña)
 */
@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String role;
    private boolean enabled;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String username, String role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
    }

}
