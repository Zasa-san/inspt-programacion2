package inspt_programacion2_kfc.backend.services.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.auth.ApiToken;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.repositories.auth.ApiTokenRepository;

@Service
@Transactional
@Slf4j
public class ApiTokenService {

    private final ApiTokenRepository repo;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiTokenService(ApiTokenRepository repo) {
        this.repo = repo;
    }

    /**
     * Generar un nuevo token API para el usuario dado.
     *
     * @param user propietario del token
     * @param validity cuánto tiempo permanece válido el token
     * @return token en texto plano generado
     */
    public String generateTokenFor(User user, Duration validity) {
        byte[] random = new byte[32];
        secureRandom.nextBytes(random);
        String token = HexFormat.of().formatHex(random);

        String hash = sha256Hex(token);

        ApiToken entity = new ApiToken();
        entity.setTokenHash(hash);
        entity.setUser(user);
        entity.setCreatedAt(Instant.now());
        if (validity != null) {
            entity.setExpiresAt(Instant.now().plus(validity));
        }
        entity.setRevoked(false);
        repo.save(entity);
        log.info("Token de usuario guardado correctamente.");

        return token;
    }

    public Optional<User> validateToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String hash = sha256Hex(token);
        return repo.findByTokenHash(hash)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt() == null || t.getExpiresAt().isAfter(Instant.now()))
                .map(ApiToken::getUser);
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(dig);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
