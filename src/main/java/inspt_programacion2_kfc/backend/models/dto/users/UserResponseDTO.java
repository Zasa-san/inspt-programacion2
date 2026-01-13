package inspt_programacion2_kfc.backend.models.dto.users;

import inspt_programacion2_kfc.backend.models.users.User;
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
    private String nombre;
    private String apellido;
    private int dni;
    private boolean presente;

    public UserResponseDTO() {
    }

    public UserResponseDTO(User usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.role = usuario.getRole().getRoleName();
        this.enabled = usuario.isEnabled();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.dni = usuario.getDni();
        this.presente = false;
    }

}
