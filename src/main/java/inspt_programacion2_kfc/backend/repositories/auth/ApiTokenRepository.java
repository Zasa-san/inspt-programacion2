package inspt_programacion2_kfc.backend.repositories.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import inspt_programacion2_kfc.backend.models.auth.ApiToken;

public interface ApiTokenRepository extends JpaRepository<ApiToken, Long> {

    Optional<ApiToken> findByTokenHash(String tokenHash);
}
