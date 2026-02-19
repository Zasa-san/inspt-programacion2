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

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.backend.models.pedidos.EstadoPedido;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.Item;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.pedidos.PedidoService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.ItemService;
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
            PedidoService pedidoService = ctx.getBean(PedidoService.class);
            ItemService itemService = ctx.getBean(ItemService.class);
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
            // ITEMS Y PRODUCTOS
            // ═══════════════════════════════════════════════════════════════
            Item piezaPollo = itemService.create("Pieza de pollo", "Pieza de pollo crudo", 12000);
            Item bastonPapa = itemService.create("Bastones de papa", "Porción de bastones de papa crudos", 3000);
            Item vaso = itemService.create("Vaso", "Vaso para bebidas", 2000);
            Item bacon = itemService.create("Bacon", "Tiras de bacon", 8000);
            Item quesoCheddar = itemService.create("Queso cheddar", "Porción de queso cheddar", 5000);
            Item tender = itemService.create("Tender", "Tender de pollo crudo", 6000);
            Item alita = itemService.create("Alita de pollo", "Alita de pollo cruda", 5000);
            Item tortilla = itemService.create("Tortilla wrap", "Tortilla de harina", 4000);
            Item helado = itemService.create("Helado", "Porción de helado", 8000);
            Item salsaBBQ = itemService.create("Salsa BBQ", "Porción de salsa BBQ", 1000);
            Item salsaRanch = itemService.create("Salsa ranch", "Porción de salsa ranch", 1000);
            Item salsaBuffalo = itemService.create("Salsa buffalo", "Porción de salsa buffalo", 1000);
            Item salsaChocolate = itemService.create("Salsa chocolate", "Porción de salsa chocolate", 1500);
            Item salsaFrutilla = itemService.create("Salsa frutilla", "Porción de salsa frutilla", 1500);
            Item salsaDulceLeche = itemService.create("Salsa dulce de leche", "Porción de dulce de leche", 2000);
            Item salsaQuesoAzul = itemService.create("Dip de queso azul", "Porción de dip de queso azul", 3000);
            Item coleslaw = itemService.create("Coleslaw", "Porción de ensalada coleslaw", 4000);
            Item purePapas = itemService.create("Puré de papas", "Porción de puré de papas", 5000);
            Item arosCebolla = itemService.create("Aros de cebolla", "Porción de aros de cebolla", 4000);
            Item jalapenos = itemService.create("Jalapeños", "Porción de jalapeños", 2000);
            Item palta = itemService.create("Palta", "Porción de palta", 8000);
            Item chipsChocolate = itemService.create("Chips de chocolate", "Topping de chips de chocolate", 3000);
            Item mani = itemService.create("Maní picado", "Topping de maní picado", 2500);
            Item cremaBatida = itemService.create("Crema batida", "Porción de crema batida", 3500);
            Item bastonesVegetales = itemService.create("Apio y zanahoria", "Bastones de apio y zanahoria", 3000);

            System.out.println("Items creados: 26 ingredientes base");

            stockService.registrarMovimiento(piezaPollo, TipoMovimiento.ENTRADA, 500, "Stock inicial", null);
            stockService.registrarMovimiento(bastonPapa, TipoMovimiento.ENTRADA, 300, "Stock inicial", null);
            stockService.registrarMovimiento(vaso, TipoMovimiento.ENTRADA, 400, "Stock inicial", null);
            stockService.registrarMovimiento(bacon, TipoMovimiento.ENTRADA, 200, "Stock inicial", null);
            stockService.registrarMovimiento(quesoCheddar, TipoMovimiento.ENTRADA, 250, "Stock inicial", null);
            stockService.registrarMovimiento(tender, TipoMovimiento.ENTRADA, 400, "Stock inicial", null);
            stockService.registrarMovimiento(alita, TipoMovimiento.ENTRADA, 300, "Stock inicial", null);
            stockService.registrarMovimiento(tortilla, TipoMovimiento.ENTRADA, 200, "Stock inicial", null);
            stockService.registrarMovimiento(helado, TipoMovimiento.ENTRADA, 150, "Stock inicial", null);
            stockService.registrarMovimiento(salsaBBQ, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);
            stockService.registrarMovimiento(salsaRanch, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);
            stockService.registrarMovimiento(salsaBuffalo, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);
            stockService.registrarMovimiento(salsaChocolate, TipoMovimiento.ENTRADA, 80, "Stock inicial", null);
            stockService.registrarMovimiento(salsaFrutilla, TipoMovimiento.ENTRADA, 80, "Stock inicial", null);
            stockService.registrarMovimiento(salsaDulceLeche, TipoMovimiento.ENTRADA, 80, "Stock inicial", null);
            stockService.registrarMovimiento(salsaQuesoAzul, TipoMovimiento.ENTRADA, 60, "Stock inicial", null);
            stockService.registrarMovimiento(coleslaw, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);
            stockService.registrarMovimiento(purePapas, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);
            stockService.registrarMovimiento(arosCebolla, TipoMovimiento.ENTRADA, 120, "Stock inicial", null);
            stockService.registrarMovimiento(jalapenos, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);
            stockService.registrarMovimiento(palta, TipoMovimiento.ENTRADA, 80, "Stock inicial", null);
            stockService.registrarMovimiento(chipsChocolate, TipoMovimiento.ENTRADA, 150, "Stock inicial", null);
            stockService.registrarMovimiento(mani, TipoMovimiento.ENTRADA, 150, "Stock inicial", null);
            stockService.registrarMovimiento(cremaBatida, TipoMovimiento.ENTRADA, 120, "Stock inicial", null);
            stockService.registrarMovimiento(bastonesVegetales, TipoMovimiento.ENTRADA, 100, "Stock inicial", null);

            System.out.println("Stock inicial registrado para todos los items");

            GrupoIngrediente grupoBase1 = new GrupoIngrediente();
            grupoBase1.setNombre("Base");
            grupoBase1.setTipo(GrupoIngrediente.TipoGrupo.OBLIGATORIO);

            Ingrediente ing1_1 = new Ingrediente();
            ing1_1.setItem(piezaPollo);
            ing1_1.setCantidad(2);
            ing1_1.setSeleccionadoPorDefecto(true);
            grupoBase1.getIngredientes().add(ing1_1);

            Ingrediente ing1_2 = new Ingrediente();
            ing1_2.setItem(bastonPapa);
            ing1_2.setCantidad(1);
            ing1_2.setSeleccionadoPorDefecto(true);
            grupoBase1.getIngredientes().add(ing1_2);

            Ingrediente ing1_3 = new Ingrediente();
            ing1_3.setItem(vaso);
            ing1_3.setCantidad(1);
            ing1_3.setSeleccionadoPorDefecto(true);
            grupoBase1.getIngredientes().add(ing1_3);

            GrupoIngrediente grupoExtras1 = new GrupoIngrediente();
            grupoExtras1.setNombre("Extras");
            grupoExtras1.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_MULTIPLE);

            Ingrediente ing1_ex1 = new Ingrediente();
            ing1_ex1.setItem(piezaPollo);
            ing1_ex1.setCantidad(2);
            ing1_ex1.setSeleccionadoPorDefecto(false);
            grupoExtras1.getIngredientes().add(ing1_ex1);

            Ingrediente ing1_ex2 = new Ingrediente();
            ing1_ex2.setItem(bacon);
            ing1_ex2.setCantidad(1);
            ing1_ex2.setSeleccionadoPorDefecto(false);
            grupoExtras1.getIngredientes().add(ing1_ex2);

            Ingrediente ing1_ex3 = new Ingrediente();
            ing1_ex3.setItem(quesoCheddar);
            ing1_ex3.setCantidad(1);
            ing1_ex3.setSeleccionadoPorDefecto(false);
            grupoExtras1.getIngredientes().add(ing1_ex3);

            List<GrupoIngrediente> gruposComboClasico = new ArrayList<>();
            gruposComboClasico.add(grupoBase1);
            gruposComboClasico.add(grupoExtras1);

            productoService.create("Combo Clásico", "Sandwich de pollo frito + papas medianas + bebida", gruposComboClasico, null, null, "/uploads/products/combo-clasico.jpg");

            GrupoIngrediente grupoBase2 = new GrupoIngrediente();
            grupoBase2.setNombre("Base");
            grupoBase2.setTipo(GrupoIngrediente.TipoGrupo.OBLIGATORIO);

            Ingrediente ing2_1 = new Ingrediente();
            ing2_1.setItem(piezaPollo);
            ing2_1.setCantidad(8);
            ing2_1.setSeleccionadoPorDefecto(true);
            grupoBase2.getIngredientes().add(ing2_1);

            Ingrediente ing2_2 = new Ingrediente();
            ing2_2.setItem(bastonPapa);
            ing2_2.setCantidad(2);
            ing2_2.setSeleccionadoPorDefecto(true);
            grupoBase2.getIngredientes().add(ing2_2);

            Ingrediente ing2_3 = new Ingrediente();
            ing2_3.setItem(vaso);
            ing2_3.setCantidad(4);
            ing2_3.setSeleccionadoPorDefecto(true);
            grupoBase2.getIngredientes().add(ing2_3);

            GrupoIngrediente grupoExtras2 = new GrupoIngrediente();
            grupoExtras2.setNombre("Extras");
            grupoExtras2.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_MULTIPLE);

            Ingrediente ing2_ex1 = new Ingrediente();
            ing2_ex1.setItem(bastonPapa);
            ing2_ex1.setCantidad(2);
            ing2_ex1.setSeleccionadoPorDefecto(false);
            grupoExtras2.getIngredientes().add(ing2_ex1);

            Ingrediente ing2_ex2 = new Ingrediente();
            ing2_ex2.setItem(salsaBBQ);
            ing2_ex2.setCantidad(1);
            ing2_ex2.setSeleccionadoPorDefecto(false);
            grupoExtras2.getIngredientes().add(ing2_ex2);

            Ingrediente ing2_ex3 = new Ingrediente();
            ing2_ex3.setItem(coleslaw);
            ing2_ex3.setCantidad(1);
            ing2_ex3.setSeleccionadoPorDefecto(false);
            grupoExtras2.getIngredientes().add(ing2_ex3);

            Ingrediente ing2_ex4 = new Ingrediente();
            ing2_ex4.setItem(purePapas);
            ing2_ex4.setCantidad(1);
            ing2_ex4.setSeleccionadoPorDefecto(false);
            grupoExtras2.getIngredientes().add(ing2_ex4);

            List<GrupoIngrediente> gruposBucket = new ArrayList<>();
            gruposBucket.add(grupoBase2);
            gruposBucket.add(grupoExtras2);

            productoService.create("Bucket Familiar", "8 piezas de pollo + 2 papas grandes + 4 bebidas", gruposBucket, null, null, "/uploads/products/bucket-familiar.jpg");

            GrupoIngrediente grupoBase3 = new GrupoIngrediente();
            grupoBase3.setNombre("Base");
            grupoBase3.setTipo(GrupoIngrediente.TipoGrupo.OBLIGATORIO);

            Ingrediente ing3_1 = new Ingrediente();
            ing3_1.setItem(tender);
            ing3_1.setCantidad(6);
            ing3_1.setSeleccionadoPorDefecto(true);
            grupoBase3.getIngredientes().add(ing3_1);

            Ingrediente ing3_2 = new Ingrediente();
            ing3_2.setItem(bastonPapa);
            ing3_2.setCantidad(1);
            ing3_2.setSeleccionadoPorDefecto(true);
            grupoBase3.getIngredientes().add(ing3_2);

            Ingrediente ing3_3 = new Ingrediente();
            ing3_3.setItem(vaso);
            ing3_3.setCantidad(1);
            ing3_3.setSeleccionadoPorDefecto(true);
            grupoBase3.getIngredientes().add(ing3_3);

            GrupoIngrediente grupoExtras3 = new GrupoIngrediente();
            grupoExtras3.setNombre("Extras");
            grupoExtras3.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_MULTIPLE);

            Ingrediente ing3_ex1 = new Ingrediente();
            ing3_ex1.setItem(salsaRanch);
            ing3_ex1.setCantidad(1);
            ing3_ex1.setSeleccionadoPorDefecto(false);
            grupoExtras3.getIngredientes().add(ing3_ex1);

            Ingrediente ing3_ex2 = new Ingrediente();
            ing3_ex2.setItem(salsaBuffalo);
            ing3_ex2.setCantidad(1);
            ing3_ex2.setSeleccionadoPorDefecto(false);
            grupoExtras3.getIngredientes().add(ing3_ex2);

            Ingrediente ing3_ex3 = new Ingrediente();
            ing3_ex3.setItem(arosCebolla);
            ing3_ex3.setCantidad(1);
            ing3_ex3.setSeleccionadoPorDefecto(false);
            grupoExtras3.getIngredientes().add(ing3_ex3);

            List<GrupoIngrediente> gruposTenders = new ArrayList<>();
            gruposTenders.add(grupoBase3);
            gruposTenders.add(grupoExtras3);

            productoService.create("Tenders Box", "6 tenders crujientes + papas + bebida + salsa a elección", gruposTenders, null, null, "/uploads/products/tenders-box.jpg");

            GrupoIngrediente grupoBase4 = new GrupoIngrediente();
            grupoBase4.setNombre("Base");
            grupoBase4.setTipo(GrupoIngrediente.TipoGrupo.OBLIGATORIO);

            Ingrediente ing4_1 = new Ingrediente();
            ing4_1.setItem(helado);
            ing4_1.setCantidad(1);
            ing4_1.setSeleccionadoPorDefecto(true);
            grupoBase4.getIngredientes().add(ing4_1);

            GrupoIngrediente grupoSalsa4 = new GrupoIngrediente();
            grupoSalsa4.setNombre("Salsa");
            grupoSalsa4.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_UNICO);

            Ingrediente ing4_s1 = new Ingrediente();
            ing4_s1.setItem(salsaChocolate);
            ing4_s1.setCantidad(1);
            ing4_s1.setSeleccionadoPorDefecto(false);
            grupoSalsa4.getIngredientes().add(ing4_s1);

            Ingrediente ing4_s2 = new Ingrediente();
            ing4_s2.setItem(salsaFrutilla);
            ing4_s2.setCantidad(1);
            ing4_s2.setSeleccionadoPorDefecto(false);
            grupoSalsa4.getIngredientes().add(ing4_s2);

            Ingrediente ing4_s3 = new Ingrediente();
            ing4_s3.setItem(salsaDulceLeche);
            ing4_s3.setCantidad(1);
            ing4_s3.setSeleccionadoPorDefecto(false);
            grupoSalsa4.getIngredientes().add(ing4_s3);

            GrupoIngrediente grupoToppings4 = new GrupoIngrediente();
            grupoToppings4.setNombre("Toppings");
            grupoToppings4.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_MULTIPLE);

            Ingrediente ing4_t1 = new Ingrediente();
            ing4_t1.setItem(chipsChocolate);
            ing4_t1.setCantidad(1);
            ing4_t1.setSeleccionadoPorDefecto(false);
            grupoToppings4.getIngredientes().add(ing4_t1);

            Ingrediente ing4_t2 = new Ingrediente();
            ing4_t2.setItem(mani);
            ing4_t2.setCantidad(1);
            ing4_t2.setSeleccionadoPorDefecto(false);
            grupoToppings4.getIngredientes().add(ing4_t2);

            Ingrediente ing4_t3 = new Ingrediente();
            ing4_t3.setItem(cremaBatida);
            ing4_t3.setCantidad(1);
            ing4_t3.setSeleccionadoPorDefecto(false);
            grupoToppings4.getIngredientes().add(ing4_t3);

            List<GrupoIngrediente> gruposHelado = new ArrayList<>();
            gruposHelado.add(grupoBase4);
            gruposHelado.add(grupoSalsa4);
            gruposHelado.add(grupoToppings4);

            productoService.create("Helado Sundae", "Helado cremoso con salsa y toppings", gruposHelado, null, null, "/uploads/products/helado-sundae.jpg");

            GrupoIngrediente grupoBase5 = new GrupoIngrediente();
            grupoBase5.setNombre("Base");
            grupoBase5.setTipo(GrupoIngrediente.TipoGrupo.OBLIGATORIO);

            Ingrediente ing5_1 = new Ingrediente();
            ing5_1.setItem(alita);
            ing5_1.setCantidad(6);
            ing5_1.setSeleccionadoPorDefecto(true);
            grupoBase5.getIngredientes().add(ing5_1);

            GrupoIngrediente grupoExtras5 = new GrupoIngrediente();
            grupoExtras5.setNombre("Extras");
            grupoExtras5.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_MULTIPLE);

            Ingrediente ing5_ex1 = new Ingrediente();
            ing5_ex1.setItem(salsaQuesoAzul);
            ing5_ex1.setCantidad(1);
            ing5_ex1.setSeleccionadoPorDefecto(false);
            grupoExtras5.getIngredientes().add(ing5_ex1);

            Ingrediente ing5_ex2 = new Ingrediente();
            ing5_ex2.setItem(bastonesVegetales);
            ing5_ex2.setCantidad(1);
            ing5_ex2.setSeleccionadoPorDefecto(false);
            grupoExtras5.getIngredientes().add(ing5_ex2);

            List<GrupoIngrediente> gruposAlitas = new ArrayList<>();
            gruposAlitas.add(grupoBase5);
            gruposAlitas.add(grupoExtras5);

            productoService.create("Alitas Picantes", "Alitas de pollo bañadas en salsa picante", gruposAlitas, null, null, "/uploads/products/alitas-picantes.jpg");

            GrupoIngrediente grupoBase6 = new GrupoIngrediente();
            grupoBase6.setNombre("Base");
            grupoBase6.setTipo(GrupoIngrediente.TipoGrupo.OBLIGATORIO);

            Ingrediente ing6_1 = new Ingrediente();
            ing6_1.setItem(tortilla);
            ing6_1.setCantidad(1);
            ing6_1.setSeleccionadoPorDefecto(true);
            grupoBase6.getIngredientes().add(ing6_1);

            Ingrediente ing6_2 = new Ingrediente();
            ing6_2.setItem(piezaPollo);
            ing6_2.setCantidad(1);
            ing6_2.setSeleccionadoPorDefecto(true);
            grupoBase6.getIngredientes().add(ing6_2);

            GrupoIngrediente grupoExtras6 = new GrupoIngrediente();
            grupoExtras6.setNombre("Extras");
            grupoExtras6.setTipo(GrupoIngrediente.TipoGrupo.OPCIONAL_MULTIPLE);

            Ingrediente ing6_ex1 = new Ingrediente();
            ing6_ex1.setItem(quesoCheddar);
            ing6_ex1.setCantidad(1);
            ing6_ex1.setSeleccionadoPorDefecto(false);
            grupoExtras6.getIngredientes().add(ing6_ex1);

            Ingrediente ing6_ex2 = new Ingrediente();
            ing6_ex2.setItem(bacon);
            ing6_ex2.setCantidad(1);
            ing6_ex2.setSeleccionadoPorDefecto(false);
            grupoExtras6.getIngredientes().add(ing6_ex2);

            Ingrediente ing6_ex3 = new Ingrediente();
            ing6_ex3.setItem(jalapenos);
            ing6_ex3.setCantidad(1);
            ing6_ex3.setSeleccionadoPorDefecto(false);
            grupoExtras6.getIngredientes().add(ing6_ex3);

            Ingrediente ing6_ex4 = new Ingrediente();
            ing6_ex4.setItem(palta);
            ing6_ex4.setCantidad(1);
            ing6_ex4.setSeleccionadoPorDefecto(false);
            grupoExtras6.getIngredientes().add(ing6_ex4);

            List<GrupoIngrediente> gruposWrap = new ArrayList<>();
            gruposWrap.add(grupoBase6);
            gruposWrap.add(grupoExtras6);

            productoService.create("Wrap de Pollo", "Tortilla de harina con pollo crispy, lechuga y salsa", gruposWrap, null, null, "/uploads/products/wrap-pollo.jpg");

            System.out.println("Productos creados: 6 productos con grupos e ingredientes");

            // ═══════════════════════════════════════════════════════════════
            // PEDIDOS DE EJEMPLO (ENTREGADOS)
            // ═══════════════════════════════════════════════════════════════
            List<ProductoEntity> productos = productoService.findAll();

            Long comboClasicoId = findProductoIdPorNombre(productos, "Combo Clásico");
            Long bucketFamiliarId = findProductoIdPorNombre(productos, "Bucket Familiar");
            Long tendersBoxId = findProductoIdPorNombre(productos, "Tenders Box");
            Long heladoSundaeId = findProductoIdPorNombre(productos, "Helado Sundae");
            Long wrapPolloId = findProductoIdPorNombre(productos, "Wrap de Pollo");

            List<CartItemDto> pedido1 = new ArrayList<>();

            pedido1.add(cartItem(comboClasicoId, 1, "Combo Clásico",
                    ing1_1.getId(), ing1_2.getId(), ing1_3.getId(),
                    ing1_ex2.getId(), ing1_ex3.getId()));

            pedido1.add(cartItem(bucketFamiliarId, 1, "Bucket Familiar",
                    ing2_1.getId(), ing2_2.getId(), ing2_3.getId(),
                    ing2_ex2.getId(), ing2_ex3.getId()));

            pedidoService.crearPedidoDesdeCarrito(pedido1, EstadoPedido.ENTREGADO);

            List<CartItemDto> pedido2 = new ArrayList<>();

            pedido2.add(cartItem(tendersBoxId, 1, "Tenders Box",
                    ing3_1.getId(), ing3_2.getId(), ing3_3.getId(),
                    ing3_ex1.getId(), ing3_ex3.getId()));

            pedido2.add(cartItem(comboClasicoId, 2, "Combo Clásico",
                    ing1_1.getId(), ing1_2.getId(), ing1_3.getId(),
                    ing1_ex1.getId()));

            pedidoService.crearPedidoDesdeCarrito(pedido2, EstadoPedido.ENTREGADO);

            List<CartItemDto> pedido3 = new ArrayList<>();

            pedido3.add(cartItem(heladoSundaeId, 2, "Helado Sundae",
                    ing4_1.getId(), ing4_s1.getId(), ing4_t1.getId(), ing4_t3.getId()));

            pedido3.add(cartItem(wrapPolloId, 1, "Wrap de Pollo",
                    ing6_1.getId(), ing6_2.getId(),
                    ing6_ex2.getId(), ing6_ex3.getId()));

            pedidoService.crearPedidoDesdeCarrito(pedido3, EstadoPedido.ENTREGADO);

            System.out.println("Pedidos entregados de ejemplo creados sobre productos existentes");

            message = "Base de datos inicializada con usuarios, turnos, asignaciones, items, stock y productos.";

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

    private static Long findProductoIdPorNombre(List<ProductoEntity> productos, String nombre) {
        return productos.stream()
                .filter(p -> p != null && p.getName() != null)
                .filter(p -> p.getName().equalsIgnoreCase(nombre))
                .map(ProductoEntity::getId)
                .findFirst()
                .orElse(null);
    }

    private static CartItemDto cartItem(Long productoId, int quantity, String productoName, Long... ingredientesIds) {
        List<Long> seleccionados = new ArrayList<>();
        if (ingredientesIds != null) {
            for (Long ingredienteId : ingredientesIds) {
                if (ingredienteId != null) {
                    seleccionados.add(ingredienteId);
                }
            }
        }

        if (seleccionados.isEmpty()) {
            return new CartItemDto(productoId, quantity, productoName);
        }

        return new CartItemDto(productoId, quantity, productoName, 0, seleccionados);
    }

}
