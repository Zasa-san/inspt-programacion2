package inspt_programacion2_kfc.security;

import java.io.IOException;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AutoLogoutOnLoginFilter extends OncePerRequestFilter {

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (AppConstants.LOGIN_URL.equals(request.getRequestURI()) && AppConstants.GET_METHOD.equalsIgnoreCase(request.getMethod())) {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {

                logoutHandler.logout(request, response, auth);
                response.sendRedirect(AppConstants.LOGIN_URL);
                return;

            }
        }

        filterChain.doFilter(request, response);
    }
}
