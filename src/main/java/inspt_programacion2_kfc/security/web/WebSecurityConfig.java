package inspt_programacion2_kfc.security.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import inspt_programacion2_kfc.backend.models.users.Role;

@Configuration
@EnableMethodSecurity
@Order(2)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login", "/access-denied", "/css/**", "/js/**", "/img/**", "/uploads/**", "/favicon.ico", "/cart/**").permitAll()
                .requestMatchers("/users/**").hasRole(Role.ROLE_ADMIN.getRoleName())
                .requestMatchers("/products/**", "/stock/**").hasRole(Role.ROLE_ADMIN.getRoleName())
                .requestMatchers("/stock/**").hasRole(Role.ROLE_ADMIN.getRoleName())
                .requestMatchers("/pedidos/**").hasAnyRole(Role.ROLE_VENDEDOR.getRoleName(), Role.ROLE_ADMIN.getRoleName(), Role.ROLE_SOPORTE.getRoleName())
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
                )
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedHandler(new AccessDeniedHandlerImpl() {
                    {
                        setErrorPage("/access-denied");
                    }
                })
                )
                .logout(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
