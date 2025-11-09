package inspt_programacion2_kfc.backend.dto.users;

/**
 * DTO para las solicitudes de creación/actualización de usuarios
 */
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
