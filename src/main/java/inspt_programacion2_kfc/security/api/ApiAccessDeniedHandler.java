package inspt_programacion2_kfc.security.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Devuelve una respuesta JSON con 403 cuando la petición a la API está
 * autenticada pero no tiene permisos para acceder al recurso.
 */
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String path = request.getRequestURI();
        String message = accessDeniedException == null ? "Access denied" : accessDeniedException.getMessage();
        String json = String.format("{\"error\":\"Forbidden\",\"message\":\"%s\",\"path\":\"%s\"}",
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
