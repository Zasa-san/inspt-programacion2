package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

@Controller
public class StockPageController {

    private final ProductoService productoService;
    private final MovimientoStockService movimientoStockService;

    public StockPageController(ProductoService productoService, MovimientoStockService movimientoStockService) {
        this.productoService = productoService;
        this.movimientoStockService = movimientoStockService;
    }

    @GetMapping("/stock")
    public String stockPage(Model model) {
        PageMetadata page = new PageMetadata("Stock de productos", "Gestión de stock por producto");
        model.addAttribute("page", page);

        List<ProductoEntity> productos = productoService.findAll();
        Map<Long, Integer> stocks = movimientoStockService.calcularStockParaProductos(productos);

        model.addAttribute("productos", productos);
        model.addAttribute("stocks", stocks);

        return "stock/index";
    }

    @PostMapping("/stock/movimiento")
    public String registrarMovimientos(
            @RequestParam("productoId") List<Long> productoIds,
            @RequestParam("tipo") List<TipoMovimiento> tipos,
            @RequestParam("cantidad") List<Integer> cantidades,
            @RequestParam(name = "motivo", required = false) List<String> motivos,
            RedirectAttributes redirectAttrs) {

        if (productoIds == null || tipos == null || cantidades == null
                || productoIds.size() != tipos.size()
                || productoIds.size() != cantidades.size()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Datos de movimiento incompletos.");
            return "redirect:/stock";
        }

        boolean algunMovimiento = false;

        for (int i = 0; i < productoIds.size(); i++) {
            Integer cantidadObj = cantidades.get(i);
            int cantidad = (cantidadObj != null) ? cantidadObj : 0;
            if (cantidad <= 0) {
                continue;
            }

            Long productoId = productoIds.get(i);
            TipoMovimiento tipo = tipos.get(i);
            String motivo = (motivos != null && motivos.size() > i) ? motivos.get(i) : null;

            ProductoEntity producto = productoService.findById(productoId);
            if (producto == null) {
                continue;
            }

            try {
                movimientoStockService.registrarMovimiento(producto, tipo, cantidad, motivo, null);
                algunMovimiento = true;
            } catch (IllegalArgumentException ex) {
                redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
            }
        }

        if (algunMovimiento) {
            redirectAttrs.addFlashAttribute("successMessage", "Movimientos registrados correctamente.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "No se registró ningún movimiento (verifique las cantidades ingresadas).");
        }

        return "redirect:/stock";
    }
}
