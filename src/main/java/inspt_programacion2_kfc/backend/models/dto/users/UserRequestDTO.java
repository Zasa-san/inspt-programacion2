package inspt_programacion2_kfc.backend.models.dto.users;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para las solicitudes de creación/actualización de usuarios
 */
@Data
public class UserRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    private String password;

    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1000000, message = "El DNI debe ser válido")
    private Integer dni;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El rol es obligatorio")
    private String role;

    private Boolean enabled;

    public UserRequestDTO() {
    }

    public UserRequestDTO(String username, String password, Integer dni, String nombre, String apellido, String role) {
        this.username = username;
        this.password = password;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.role = role;
        this.enabled = true;
    }

}
