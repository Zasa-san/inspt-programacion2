package inspt_programacion2_kfc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.users.Role;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login", "/access-denied", "/css/**", "/js/**", "/img/**", "/uploads/**", "/favicon.ico", "/cart/**", "/checkout/**").permitAll()
                .requestMatchers("/users/change-password").authenticated()
                .requestMatchers("/users").authenticated()
                .requestMatchers("/users/**").hasRole(Role.ROLE_ADMIN.getRoleName())
                .requestMatchers("/products/**").hasRole(Role.ROLE_ADMIN.getRoleName())
                .requestMatchers("/stock/**").hasAnyRole(Role.ROLE_ADMIN.getRoleName(), Role.ROLE_SOPORTE.getRoleName())
                .requestMatchers("/pedidos/**").hasAnyRole(Role.ROLE_ADMIN.getRoleName(), Role.ROLE_VENDEDOR.getRoleName(), Role.ROLE_SOPORTE.getRoleName())
                .anyRequest().authenticated()
        )
                .formLogin(form -> form
                .loginPage(AppConstants.LOGIN_URL)
                .defaultSuccessUrl("/", true)
                .permitAll()
                )
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(AppConstants.LOGIN_URL))
                .accessDeniedHandler(new AccessDeniedHandlerImpl() {
                    {
                        setErrorPage("/access-denied");
                    }
                })
                )
                .logout(withDefaults())
                .headers(headers -> headers.cacheControl(withDefaults()));

        http.addFilterBefore(new NoStoreForLoginFilter(),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new AutoLogoutFilter(),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
