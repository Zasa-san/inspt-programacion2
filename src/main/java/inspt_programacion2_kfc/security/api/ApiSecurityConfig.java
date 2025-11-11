package inspt_programacion2_kfc.security.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import inspt_programacion2_kfc.backend.models.users.Role;

@Configuration
@Order(1)
public class ApiSecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
            ApiAuthenticationEntryPoint entryPoint,
            ApiAccessDeniedHandler deniedHandler,
            ApiTokenAuthenticationFilter tokenFilter) throws Exception {

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/**").hasRole(Role.ROLE_ADMIN.getRoleName())
                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(deniedHandler)
                );

        return http.build();
    }
}
