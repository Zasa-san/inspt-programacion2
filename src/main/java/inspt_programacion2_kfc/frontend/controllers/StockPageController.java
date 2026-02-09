package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.ingredients.IngredienteEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.services.ingredients.IngredienteService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

@Controller
public class StockPageController {

    private final IngredienteService ingredienteService;
    private final MovimientoStockService movimientoStockService;

    public StockPageController(IngredienteService ingredienteService, MovimientoStockService movimientoStockService) {
        this.ingredienteService = ingredienteService;
        this.movimientoStockService = movimientoStockService;
    }

    @GetMapping("/stock")
    public String stockPage(Model model) {
        PageMetadata page = new PageMetadata("Stock de ingredientes", "Gestión de stock por ingrediente");
        model.addAttribute("page", page);

        List<IngredienteEntity> ingredientes = ingredienteService.findAllActive();
        Map<Long, Integer> stocks = movimientoStockService.calcularStockParaIngredientes(ingredientes);

        model.addAttribute("ingredientes", ingredientes);
        model.addAttribute("stocks", stocks);

        return "stock/index";
    }

    @PostMapping("/stock/movimiento")
    public String registrarMovimientos(
            @RequestParam("ingredienteId") List<Long> ingredienteIds,
            @RequestParam("tipo") List<TipoMovimiento> tipos,
            @RequestParam("cantidad") List<Integer> cantidades,
            @RequestParam(name = "motivo", required = false) List<String> motivos,
            RedirectAttributes redirectAttrs) {

        if (ingredienteIds == null || tipos == null || cantidades == null
                || ingredienteIds.size() != tipos.size()
                || ingredienteIds.size() != cantidades.size()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Datos de movimiento incompletos.");
            return "redirect:/stock";
        }

        boolean algunMovimiento = false;

        for (int i = 0; i < ingredienteIds.size(); i++) {
            Integer cantidadObj = cantidades.get(i);
            int cantidad = (cantidadObj != null) ? cantidadObj : 0;
            if (cantidad <= 0) {
                continue;
            }

            Long ingredienteId = ingredienteIds.get(i);
            TipoMovimiento tipo = tipos.get(i);
            String motivo = (motivos != null && motivos.size() > i) ? motivos.get(i) : null;

            IngredienteEntity ingrediente = ingredienteService.findById(ingredienteId);
            if (ingrediente == null) {
                continue;
            }

            try {
                movimientoStockService.registrarMovimiento(ingrediente, tipo, cantidad, motivo, null);
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

