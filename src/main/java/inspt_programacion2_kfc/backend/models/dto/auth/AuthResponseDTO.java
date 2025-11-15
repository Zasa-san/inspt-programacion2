package inspt_programacion2_kfc.backend.models.dto.auth;

import lombok.Getter;

@Getter
public class AuthResponseDTO {
    private final String username;
    private final String message;

    public AuthResponseDTO(String username, String message) {
        this.username = username;
        this.message = message;
    }

}