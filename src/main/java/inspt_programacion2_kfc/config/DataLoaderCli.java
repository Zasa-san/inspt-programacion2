package inspt_programacion2_kfc.config;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.zaxxer.hikari.HikariDataSource;

import inspt_programacion2_kfc.model.Role;
import inspt_programacion2_kfc.service.UserService;

public class DataLoaderCli {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(inspt_programacion2_kfc.InsptProgramacion2KfcApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
        String message = null;
        Throwable error = null;

        try {
            UserService service = ctx.getBean(UserService.class);
            var env = ctx.getEnvironment();
            String username = env.getProperty("app.default-user.username", "Gerente1");
            String password = env.getProperty("app.default-user.password", "Gerente1");
            String roleStr = env.getProperty("app.default-user.role", "ROLE_ADMIN");
            Role role = Role.valueOf(roleStr);
            service.create(username, password, role, true);
            message = "Se ha inicializado el usuario por defecto: " + username;
        } catch (BeansException e) {
            message = "Error al crear usuario por defecto: " + e.getMessage();
            error = e;
        } finally {
            try {
                DataSource ds = ctx.getBean(DataSource.class);
                if (ds instanceof HikariDataSource hikariDataSource) {
                    try {
                        hikariDataSource.close();
                    } catch (Exception ex) {
                        // ignore and continue
                    }
                }
            } catch (BeansException t) {
                // ignore and continue
            }

            SpringApplication.exit(ctx, () -> 0);

            if (message != null) {
                System.out.println(message);
                System.exit(0);
            }
            if (error != null) {
                error.printStackTrace(System.err);
            }
            System.exit(1);
        }
    }
}
