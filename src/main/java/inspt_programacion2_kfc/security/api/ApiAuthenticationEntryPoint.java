package inspt_programacion2_kfc.security.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Devuelve una respuesta JSON con 401 cuando la petición a la API no está
 * autenticada.
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String path = request.getRequestURI();
        String message = authException == null ? "Unauthorized" : authException.getMessage();
        String json = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                escapeJson(message), escapeJson(path));

        response.getWriter().write(json);
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
