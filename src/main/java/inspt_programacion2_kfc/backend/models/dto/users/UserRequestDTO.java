package inspt_programacion2_kfc.backend.models.dto.users;

import lombok.Data;

/**
 * DTO para las solicitudes de creación/actualización de usuarios
 */
@Data
public class UserRequestDTO {

    private String username;
    private String password;
    private String role;

    public UserRequestDTO() {
    }

    public UserRequestDTO(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
