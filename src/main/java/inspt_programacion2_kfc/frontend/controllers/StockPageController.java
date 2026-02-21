package inspt_programacion2_kfc.frontend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.stock.Item;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.services.files.CsvService;
import inspt_programacion2_kfc.backend.services.stock.ItemService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

@Controller
public class StockPageController {

    private final ItemService itemService;
    private final MovimientoStockService movimientoStockService;
    private final CsvService csvExportService;

    public StockPageController(ItemService itemService, MovimientoStockService movimientoStockService,
            CsvService csvExportService) {
        this.itemService = itemService;
        this.movimientoStockService = movimientoStockService;
        this.csvExportService = csvExportService;
    }

    @GetMapping("/stock")
    public String stockPage(Model model) {
        PageMetadata page = new PageMetadata("Stock de items", "Gestión de movimientos de stock");
        model.addAttribute("page", page);

        List<Item> items = itemService.findAll();
        Map<Long, Integer> stocks = new HashMap<>();
        for (Item item : items) {
            stocks.put(item.getId(), movimientoStockService.calcularStockItem(item.getId()));
        }

        model.addAttribute("items", items);
        model.addAttribute("stocks", stocks);

        return "stock/index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/stock/movimientos/export.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportarMovimientosCsv() {
        byte[] csv = csvExportService.exportarMovimientosStockCsv();

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movimientos-stock.csv")
                .body(csv);
    }

    @PostMapping("/stock/movimiento")
    public String registrarMovimientos(
            @RequestParam("itemId") List<Long> itemIds,
            @RequestParam("tipo") List<TipoMovimiento> tipos,
            @RequestParam("cantidad") List<Integer> cantidades,
            @RequestParam(name = "motivo", required = false) List<String> motivos,
            RedirectAttributes redirectAttrs) {

        if (itemIds == null || tipos == null || cantidades == null
                || itemIds.size() != tipos.size()
                || itemIds.size() != cantidades.size()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Datos de movimiento incompletos.");
            return "redirect:/stock";
        }

        boolean algunMovimiento = false;
        String errorMsg = null;

        for (int i = 0; i < itemIds.size(); i++) {
            Integer cantidadObj = cantidades.get(i);
            int cantidad = (cantidadObj != null) ? cantidadObj : 0;
            if (cantidad <= 0) {
                continue;
            }

            Long itemId = itemIds.get(i);
            TipoMovimiento tipo = tipos.get(i);
            String motivo = (motivos != null && motivos.size() > i) ? motivos.get(i) : null;

            try {
                Item item = itemService.findById(itemId);
                movimientoStockService.registrarMovimiento(item, tipo, cantidad, motivo, null);
                algunMovimiento = true;
            } catch (RuntimeException ex) {
                errorMsg = ex.getMessage();
            }
        }

        if (errorMsg != null) {
            redirectAttrs.addFlashAttribute("errorMessage", errorMsg);
        } else if (algunMovimiento) {
            redirectAttrs.addFlashAttribute("successMessage", "Movimientos registrados correctamente.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "No se registró ningún movimiento (verifique las cantidades ingresadas).");
        }

        return "redirect:/stock";
    }
}
