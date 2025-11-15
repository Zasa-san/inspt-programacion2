package inspt_programacion2_kfc.frontend.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

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

        var productos = productoService.findAll();
        Map<Long, Integer> stocks = movimientoStockService.calcularStockParaProductos(productos);

        model.addAttribute("productos", productos);
        model.addAttribute("stocks", stocks);

        return "stock/index";
    }

    @PostMapping("/stock/movimiento")
    public String registrarMovimiento(@RequestParam("productoId") Long productoId,
            @RequestParam("tipo") TipoMovimiento tipo,
            @RequestParam("cantidad") int cantidad,
            @RequestParam(name = "motivo", required = false) String motivo,
            RedirectAttributes redirectAttrs) {

        var productoOpt = productoService.findById(productoId);
        if (productoOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/stock";
        }

        try {
            movimientoStockService.registrarMovimiento(productoOpt.get(), tipo, cantidad, motivo, null);
            redirectAttrs.addFlashAttribute("successMessage", "Movimiento registrado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/stock";
    }
}


