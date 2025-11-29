package inspt_programacion2_kfc.config;

import com.zaxxer.hikari.HikariDataSource;
import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.products.TipoCustomizacion;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.services.products.CustomizacionesService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.users.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

@Slf4j
public class DataLoaderCli {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(inspt_programacion2_kfc.InsptProgramacion2KfcApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
        String message = null;

        try {
            UserService service = ctx.getBean(UserService.class);

            service.create("admin", "admin", Role.ROLE_ADMIN, true);
            service.create("ventas", "ventas", Role.ROLE_VENDEDOR, true);
            service.create("soporte", "soporte", Role.ROLE_SOPORTE, true);

            // Productos de ejemplo
            ProductoService productoService = ctx.getBean(ProductoService.class);
            CustomizacionesService customizacionesService = ctx.getBean(CustomizacionesService.class);

            // Producto 1: Combo Clásico (sin customizaciones)
            ProductoEntity p1 = new ProductoEntity();
            p1.setName("Combo Clásico");
            p1.setDescription("Sandwich de pollo frito + papas medianas + bebida.");
            p1.setPrice(55000);
            p1.setAvailable(true);
            p1.setImgUrl("/uploads/products/combo-clasico.jpg");
            productoService.create(p1);

            // Producto 2: Bucket Familiar (con customizaciones)
            ProductoEntity p2 = new ProductoEntity();
            p2.setName("Bucket Familiar");
            p2.setDescription("8 piezas de pollo + 2 papas grandes + 4 bebidas.");
            p2.setPrice(129000);
            p2.setAvailable(true);
            p2.setImgUrl("/uploads/products/bucket-familiar.jpg");
            productoService.create(p2);

            // Customizaciones para Bucket Familiar
            CustomizacionEntity c21 = new CustomizacionEntity();
            c21.setProducto(p2);
            c21.setNombre("Mediano");
            c21.setPriceModifier(10000);
            c21.setTipo(TipoCustomizacion.UNICA);
            customizacionesService.create(c21);

            CustomizacionEntity c22 = new CustomizacionEntity();
            c22.setProducto(p2);
            c22.setNombre("Grande");
            c22.setPriceModifier(20000);
            c22.setTipo(TipoCustomizacion.UNICA);
            customizacionesService.create(c22);

            // Producto 3: Tenders Box (con customizaciones)
            ProductoEntity p3 = new ProductoEntity();
            p3.setName("Tenders Box");
            p3.setDescription("6 tenders + papas chicas + bebida.");
            p3.setPrice(62000);
            p3.setAvailable(false);
            p3.setImgUrl("/uploads/products/tenders-box.jpg");
            productoService.create(p3);

            // Customizaciones para Tenders Box
            CustomizacionEntity c31 = new CustomizacionEntity();
            c31.setProducto(p3);
            c31.setNombre("Grande");
            c31.setPriceModifier(20000);
            c31.setTipo(TipoCustomizacion.UNICA);
            customizacionesService.create(c31);

            CustomizacionEntity c32 = new CustomizacionEntity();
            c32.setProducto(p3);
            c32.setNombre("Mediano");
            c32.setPriceModifier(10000);
            c32.setTipo(TipoCustomizacion.UNICA);
            customizacionesService.create(c32);

            // Producto 4: Helado Sundae (sin customizaciones)
            ProductoEntity p4 = new ProductoEntity();
            p4.setName("Helado Sundae");
            p4.setDescription("Postre helado con salsa a elección.");
            p4.setAvailable(true);
            p4.setPrice(150000);
            p4.setImgUrl("/uploads/products/helado-sundae.jpg");
            productoService.create(p4);

            message = "Se ha inicializado los usuarios por defecto y productos de ejemplo.";
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
