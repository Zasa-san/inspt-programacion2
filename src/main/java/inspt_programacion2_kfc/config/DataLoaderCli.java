package inspt_programacion2_kfc.config;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.zaxxer.hikari.HikariDataSource;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.users.UserService;

@Slf4j
public class DataLoaderCli {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(inspt_programacion2_kfc.InsptProgramacion2KfcApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
        String message = null;

        try {
            UserService service = ctx.getBean(UserService.class);
            var env = ctx.getEnvironment();
            String username = env.getProperty("app.default-user.username", "admin");
            String password = env.getProperty("app.default-user.password", "admin");
            String roleStr = env.getProperty("app.default-user.role", "ROLE_ADMIN");
            Role role = Role.valueOf(roleStr);
            service.create(username, password, role, true);

            // Productos de ejemplo
            ProductoService productoService = ctx.getBean(ProductoService.class);

            if (productoService.findAll().isEmpty()) {
                ProductoEntity p1 = new ProductoEntity();
                p1.setName("Combo Clásico");
                p1.setDescription("Sandwich de pollo frito + papas medianas + bebida.");
                p1.setPrice(55000);
                p1.setAvailable(true);
                p1.setImgUrl("/uploads/products/combo-clasico.jpg");

                ProductoEntity p2 = new ProductoEntity();
                p2.setName("Bucket Familiar");
                p2.setDescription("8 piezas de pollo + 2 papas grandes + 4 bebidas.");
                p2.setPrice(129000);
                p2.setAvailable(true);
                p2.setImgUrl("/uploads/products/bucket-familiar.jpg");

                ProductoEntity p3 = new ProductoEntity();
                p3.setName("Tenders Box");
                p3.setDescription("6 tenders + papas chicas + bebida.");
                p3.setPrice(62000);
                p3.setAvailable(false);
                p3.setImgUrl("/uploads/products/tenders-box.jpg");

                ProductoEntity p4 = new ProductoEntity();
                p4.setName("Helado Sundae");
                p4.setDescription("Postre helado con salsa a elección.");
                p4.setAvailable(true);
                p4.setPrice(150000);
                p4.setImgUrl("/uploads/products/helado-sundae.jpg");

                productoService.save(p1);
                productoService.save(p2);
                productoService.save(p3);
                productoService.save(p4);
            }

            message = "Se ha inicializado el usuario por defecto y productos de ejemplo.";
        } catch (BeansException e) {
            log.error("Error inicializando la bdd", e);
        } finally {
            try {
                DataSource ds = ctx.getBean(DataSource.class);
                if (ds instanceof HikariDataSource hikariDataSource) {
                    try {
                        hikariDataSource.close();
                    } catch (Exception ex) {
                        log.error("Error inicializando la bdd", ex);
                    }
                }
            } catch (BeansException t) {
                log.error("Error inicializando la bdd", t);
            }

            SpringApplication.exit(ctx, () -> 0);

            if (message != null) {
                System.out.println(message);
                System.exit(0);
            }

            System.exit(1);
        }
    }
}
