package inspt_programacion2_kfc.api.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.auth.ApiTokenService;

@RestController
@RequestMapping("/api/auth")
public class ApiTokenController {

    private final ApiTokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public ApiTokenController(ApiTokenService tokenService, AuthenticationConfiguration authConfig) throws Exception {
        this.tokenService = tokenService;
        this.authenticationManager = authConfig.getAuthenticationManager();
    }

    /**
     * Crear un nuevo token API para el usuario autenticado. El token tendrá una
     * validez predeterminada de 30 días.
     */
    @PostMapping("/token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> createToken(@AuthenticationPrincipal User user) {
        String token = tokenService.generateTokenFor(user, Duration.ofDays(30));
        Instant expires = Instant.now().plus(Duration.ofDays(30));
        return ResponseEntity.ok(Map.of("token", token, "expiresAt", expires.toString()));
    }

    /**
     * Login por credenciales: acepta JSON {"username":"...","password":"..."} y
     * devuelve un token plaintext si las credenciales son válidas.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password required"));
        }

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                String token = tokenService.generateTokenFor(user, Duration.ofDays(30));
                Instant expires = Instant.now().plus(Duration.ofDays(30));
                return ResponseEntity.ok(Map.of("token", token, "expiresAt", expires.toString()));
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "unexpected principal type"));
            }
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }
    }
}
