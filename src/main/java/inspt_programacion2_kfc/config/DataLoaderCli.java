package inspt_programacion2_kfc.config;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.zaxxer.hikari.HikariDataSource;

import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.products.TipoCustomizacion;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.products.CustomizacionesService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.backend.services.users.AsignacionTurnoService;
import inspt_programacion2_kfc.backend.services.users.TurnoService;
import inspt_programacion2_kfc.backend.services.users.UserService;

public class DataLoaderCli {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(inspt_programacion2_kfc.InsptProgramacion2KfcApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
        String message = null;

        try {
            UserService userService = ctx.getBean(UserService.class);
            ProductoService productoService = ctx.getBean(ProductoService.class);
            CustomizacionesService customizacionesService = ctx.getBean(CustomizacionesService.class);
            MovimientoStockService stockService = ctx.getBean(MovimientoStockService.class);
            TurnoService turnoService = ctx.getBean(TurnoService.class);
            AsignacionTurnoService asignacionTurnoService = ctx.getBean(AsignacionTurnoService.class);

            // ═══════════════════════════════════════════════════════════════
            // TURNOS: Crear turnos para los 7 días de la semana
            // ═══════════════════════════════════════════════════════════════
            List<Turno> turnosMañana = new ArrayList<>();
            List<Turno> turnosTarde = new ArrayList<>();
            List<Turno> turnosNoche = new ArrayList<>();

            for (int dia = 1; dia <= 7; dia++) {
                // Turno mañana: 6:00 - 12:00
                Turno mañana = turnoService.create(Time.valueOf("06:00:00"), Time.valueOf("12:00:00"), dia);
                turnosMañana.add(mañana);

                // Turno tarde: 12:00 - 18:00
                Turno tarde = turnoService.create(Time.valueOf("12:00:00"), Time.valueOf("18:00:00"), dia);
                turnosTarde.add(tarde);

                // Turno noche: 18:00 - 00:00
                Turno noche = turnoService.create(Time.valueOf("18:00:00"), Time.valueOf("00:00:00"), dia);
                turnosNoche.add(noche);
            }
            System.out.println("Turnos creados: 21 turnos (7 días x 3 franjas horarias)");

            // ═══════════════════════════════════════════════════════════════
            // USUARIOS
            // ═══════════════════════════════════════════════════════════════
            userService.create("admin", "admin", 11111111, "Admin", "Sistema", Role.ROLE_ADMIN, true);
            userService.create("ventas", "ventas", 22222222, "Ventas", "General", Role.ROLE_VENDEDOR, true);
            userService.create("soporte", "soporte", 33333333, "Soporte", "General", Role.ROLE_SOPORTE, true);

            // Usuarios con datos completos
            User vendedor1 = userService.create("jperez", "pass123", 12345678, "Juan", "Pérez", Role.ROLE_VENDEDOR, true);
            User vendedor2 = userService.create("mgarcia", "pass123", 23456789, "María", "García", Role.ROLE_VENDEDOR, true);
            User soporte1 = userService.create("lrodriguez", "pass123", 34567890, "Luis", "Rodríguez", Role.ROLE_SOPORTE, true);
            User soporte2 = userService.create("agomez", "pass123", 45678901, "Ana", "Gómez", Role.ROLE_SOPORTE, true);

            // ═══════════════════════════════════════════════════════════════
            // ASIGNACIÓN DE TURNOS
            // ═══════════════════════════════════════════════════════════════
            Timestamp inicioVigencia = Timestamp.valueOf("2025-01-01 00:00:00");

            // Vendedor1: Turnos de mañana de lunes a viernes (días 1-5)
            for (int i = 0; i < 5; i++) {
                asignacionTurnoService.asignarTurno(vendedor1, turnosMañana.get(i), inicioVigencia, true);
            }

            // Vendedor2: Turnos de tarde de lunes a viernes (días 1-5)
            for (int i = 0; i < 5; i++) {
                asignacionTurnoService.asignarTurno(vendedor2, turnosTarde.get(i), inicioVigencia, true);
            }

            // Soporte1: Turnos de noche de lunes a viernes (días 1-5)
            for (int i = 0; i < 5; i++) {
                asignacionTurnoService.asignarTurno(soporte1, turnosNoche.get(i), inicioVigencia, true);
            }

            // Soporte2: Turnos de fin de semana (sábado y domingo = días 6 y 7, todas las franjas)
            asignacionTurnoService.asignarTurno(soporte2, turnosMañana.get(5), inicioVigencia, true);
            asignacionTurnoService.asignarTurno(soporte2, turnosTarde.get(5), inicioVigencia, true);
            asignacionTurnoService.asignarTurno(soporte2, turnosNoche.get(5), inicioVigencia, true);
            asignacionTurnoService.asignarTurno(soporte2, turnosMañana.get(6), inicioVigencia, true);
            asignacionTurnoService.asignarTurno(soporte2, turnosTarde.get(6), inicioVigencia, true);
            asignacionTurnoService.asignarTurno(soporte2, turnosNoche.get(6), inicioVigencia, true);

            System.out.println("Asignaciones de turno creadas para todos los usuarios");

            // ═══════════════════════════════════════════════════════════════
            // PRODUCTO 1: Combo Clásico
            // ═══════════════════════════════════════════════════════════════
            ProductoEntity p1 = new ProductoEntity();
            p1.setName("Combo Clásico");
            p1.setDescription("Sandwich de pollo frito + papas medianas + bebida.");
            p1.setPrice(55000);
            p1.setAvailable(true);
            p1.setImgUrl("/uploads/products/combo-clasico.jpg");
            productoService.create(p1);

            // Tamaños (UNICA)
            crearCustomizacion(customizacionesService, p1, "Combo Chico", 0, TipoCustomizacion.UNICA, "Tamaño");
            crearCustomizacion(customizacionesService, p1, "Combo Mediano", 15000, TipoCustomizacion.UNICA, "Tamaño");
            crearCustomizacion(customizacionesService, p1, "Combo Grande", 25000, TipoCustomizacion.UNICA, "Tamaño");

            // Extras (MULTIPLE)
            crearCustomizacion(customizacionesService, p1, "Doble carne", 35000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p1, "Bacon crispy", 12000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p1, "Queso cheddar extra", 8000, TipoCustomizacion.MULTIPLE, "Extra");

            // Stock inicial: 25 unidades
            stockService.registrarMovimiento(p1, TipoMovimiento.ENTRADA, 25, "Stock inicial", null);

            // ═══════════════════════════════════════════════════════════════
            // PRODUCTO 2: Bucket Familiar
            // ═══════════════════════════════════════════════════════════════
            ProductoEntity p2 = new ProductoEntity();
            p2.setName("Bucket Familiar");
            p2.setDescription("8 piezas de pollo + 2 papas grandes + 4 bebidas.");
            p2.setPrice(129000);
            p2.setAvailable(true);
            p2.setImgUrl("/uploads/products/bucket-familiar.jpg");
            productoService.create(p2);

            // Tamaños (UNICA)
            crearCustomizacion(customizacionesService, p2, "8 piezas", 0, TipoCustomizacion.UNICA, "Piezas");
            crearCustomizacion(customizacionesService, p2, "12 piezas", 45000, TipoCustomizacion.UNICA, "Piezas");
            crearCustomizacion(customizacionesService, p2, "16 piezas", 75000, TipoCustomizacion.UNICA, "Piezas");

            // Extras (MULTIPLE)
            crearCustomizacion(customizacionesService, p2, "Papas XL", 18000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p2, "Salsa BBQ extra", 5000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p2, "Coleslaw", 12000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p2, "Puré de papas", 15000, TipoCustomizacion.MULTIPLE, "Extra");

            // Stock inicial: 15 unidades
            stockService.registrarMovimiento(p2, TipoMovimiento.ENTRADA, 15, "Stock inicial", null);

            // ═══════════════════════════════════════════════════════════════
            // PRODUCTO 3: Tenders Box
            // ═══════════════════════════════════════════════════════════════
            ProductoEntity p3 = new ProductoEntity();
            p3.setName("Tenders Box");
            p3.setDescription("6 tenders crujientes + papas + bebida + salsa a elección.");
            p3.setPrice(62000);
            p3.setAvailable(true);
            p3.setImgUrl("/uploads/products/tenders-box.jpg");
            productoService.create(p3);

            // Tamaños (UNICA)
            crearCustomizacion(customizacionesService, p3, "6 tenders", 0, TipoCustomizacion.UNICA, "Piezas");
            crearCustomizacion(customizacionesService, p3, "9 tenders", 22000, TipoCustomizacion.UNICA, "Piezas");
            crearCustomizacion(customizacionesService, p3, "12 tenders", 38000, TipoCustomizacion.UNICA, "Piezas");

            // Extras (MULTIPLE)
            crearCustomizacion(customizacionesService, p3, "Salsa ranch extra", 4000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p3, "Salsa buffalo", 4000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p3, "Aros de cebolla", 15000, TipoCustomizacion.MULTIPLE, "Extra");

            // Stock inicial: 30 unidades
            stockService.registrarMovimiento(p3, TipoMovimiento.ENTRADA, 30, "Stock inicial", null);

            // ═══════════════════════════════════════════════════════════════
            // PRODUCTO 4: Helado Sundae
            // ═══════════════════════════════════════════════════════════════
            ProductoEntity p4 = new ProductoEntity();
            p4.setName("Helado Sundae");
            p4.setDescription("Helado cremoso con salsa y toppings.");
            p4.setAvailable(true);
            p4.setPrice(25000);
            p4.setImgUrl("/uploads/products/helado-sundae.jpg");
            productoService.create(p4);

            // Sabor salsa (UNICA)
            crearCustomizacion(customizacionesService, p4, "Salsa chocolate", 0, TipoCustomizacion.UNICA, "Salsa");
            crearCustomizacion(customizacionesService, p4, "Salsa frutilla", 0, TipoCustomizacion.UNICA, "Salsa");
            crearCustomizacion(customizacionesService, p4, "Salsa dulce de leche", 3000, TipoCustomizacion.UNICA, "Salsa");

            // Toppings (MULTIPLE)
            crearCustomizacion(customizacionesService, p4, "Chips de chocolate", 5000, TipoCustomizacion.MULTIPLE, "Toppings");
            crearCustomizacion(customizacionesService, p4, "Maní picado", 4000, TipoCustomizacion.MULTIPLE, "Toppings");
            crearCustomizacion(customizacionesService, p4, "Crema batida", 6000, TipoCustomizacion.MULTIPLE, "Toppings");

            // Stock inicial: 50 unidades
            stockService.registrarMovimiento(p4, TipoMovimiento.ENTRADA, 50, "Stock inicial", null);

            // ═══════════════════════════════════════════════════════════════
            // PRODUCTO 5: Alitas Picantes
            // ═══════════════════════════════════════════════════════════════
            ProductoEntity p5 = new ProductoEntity();
            p5.setName("Alitas Picantes");
            p5.setDescription("Alitas de pollo bañadas en salsa picante.");
            p5.setAvailable(true);
            p5.setPrice(48000);
            p5.setImgUrl("/uploads/products/alitas-picantes.jpg");
            productoService.create(p5);

            // Cantidad (UNICA)
            crearCustomizacion(customizacionesService, p5, "6 alitas", 0, TipoCustomizacion.UNICA, "Piezas");
            crearCustomizacion(customizacionesService, p5, "12 alitas", 42000, TipoCustomizacion.UNICA, "Piezas");
            crearCustomizacion(customizacionesService, p5, "18 alitas", 78000, TipoCustomizacion.UNICA, "Piezas");

            // Nivel picante (UNICA)
            crearCustomizacion(customizacionesService, p5, "Suave", 0, TipoCustomizacion.UNICA, "Picante");
            crearCustomizacion(customizacionesService, p5, "Medio ️", 0, TipoCustomizacion.UNICA, "Picante");
            crearCustomizacion(customizacionesService, p5, "Infernall️", 0, TipoCustomizacion.UNICA, "Picante");

            // Extras (MULTIPLE)
            crearCustomizacion(customizacionesService, p5, "Dip de queso azul", 8000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p5, "Apio y zanahoria", 6000, TipoCustomizacion.MULTIPLE, "Extra");

            // Stock inicial: 20 unidades
            stockService.registrarMovimiento(p5, TipoMovimiento.ENTRADA, 20, "Stock inicial", null);

            // ═══════════════════════════════════════════════════════════════
            // PRODUCTO 6: Wrap de Pollo
            // ═══════════════════════════════════════════════════════════════
            ProductoEntity p6 = new ProductoEntity();
            p6.setName("Wrap de Pollo");
            p6.setDescription("Tortilla de harina con pollo crispy, lechuga y salsa.");
            p6.setAvailable(true);
            p6.setPrice(42000);
            p6.setImgUrl("/uploads/products/wrap-pollo.jpg");
            productoService.create(p6);

            // Tipo de pollo (UNICA)
            crearCustomizacion(customizacionesService, p6, "Pollo crispy", 0, TipoCustomizacion.UNICA, "Cocción");
            crearCustomizacion(customizacionesService, p6, "Pollo grillé", 5000, TipoCustomizacion.UNICA, "Cocción");

            // Extras (MULTIPLE)
            crearCustomizacion(customizacionesService, p6, "Queso cheddar", 7000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p6, "Bacon", 10000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p6, "Jalapeños", 4000, TipoCustomizacion.MULTIPLE, "Extra");
            crearCustomizacion(customizacionesService, p6, "Palta", 12000, TipoCustomizacion.MULTIPLE, "Extra");

            // Stock inicial: 35 unidades
            stockService.registrarMovimiento(p6, TipoMovimiento.ENTRADA, 35, "Stock inicial", null);

            message = "Base de datos inicializada con usuarios, turnos, asignaciones, productos, customizaciones y stock.";

        } catch (BeansException e) {
            System.err.printf("Error inicializando la bdd %s", e.getMessage());
        } finally {
            try {
                DataSource ds = ctx.getBean(DataSource.class);
                if (ds instanceof HikariDataSource hikariDataSource) {
                    try {
                        hikariDataSource.close();
                    } catch (Exception ex) {
                        System.err.printf("Error cerrando conexión %s", ex.getMessage());
                    }
                }
            } catch (BeansException t) {
                System.err.printf("Error obteniendo DataSource %s", t.getMessage());
            }

            SpringApplication.exit(ctx, () -> 0);

            if (message != null) {
                System.out.println(message);
                System.exit(0);
            }

            System.exit(1);
        }
    }

    private static void crearCustomizacion(
            CustomizacionesService service,
            ProductoEntity producto,
            String nombre,
            int precio,
            TipoCustomizacion tipo,
            String grupo) {
        CustomizacionEntity c = new CustomizacionEntity();
        c.setProducto(producto);
        c.setNombre(nombre);
        c.setPriceModifier(precio);
        c.setTipo(tipo);
        c.setGrupo(grupo);
        service.create(c);
    }
}
