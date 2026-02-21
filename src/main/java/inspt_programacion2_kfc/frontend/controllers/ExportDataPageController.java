package inspt_programacion2_kfc.frontend.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.services.files.CsvService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class ExportDataPageController {

    private final CsvService csvService;

    public ExportDataPageController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/exportar-datos")
    public String exportarDatosPage(
            @RequestParam(name = "fechaDesde", required = false) String fechaDesdeParam,
            @RequestParam(name = "fechaHasta", required = false) String fechaHastaParam,
            Model model) {

        LocalDate hoy = LocalDate.now();
        LocalDate desde = parseDateOrDefault(fechaDesdeParam, hoy.minusDays(6));
        LocalDate hasta = parseDateOrDefault(fechaHastaParam, hoy);

        if (desde.isAfter(hasta)) {
            model.addAttribute("errorMessage", "El rango de fechas es inválido: 'Desde' no puede ser mayor que 'Hasta'.");
        }

        PageMetadata page = new PageMetadata("Exportar datos", "Descarga de ventas y movimientos de stock por rango de fechas");
        model.addAttribute("page", page);
        model.addAttribute("fechaDesde", desde);
        model.addAttribute("fechaHasta", hasta);

        return "exportaciones/index";
    }

    @GetMapping(value = "/exportar-datos/ventas.csv", produces = "text/csv")
    public Object exportarVentasCsv(
            @RequestParam(name = "fechaDesde", required = false) String fechaDesdeParam,
            @RequestParam(name = "fechaHasta", required = false) String fechaHastaParam,
            RedirectAttributes redirectAttributes) {

        LocalDate fechaDesde = parseDate(fechaDesdeParam);
        LocalDate fechaHasta = parseDate(fechaHastaParam);

        if (fechaDesde == null || fechaHasta == null) {
            return redirectConError("Formato de fecha inválido. Usá un formato válido de fecha.",
                    fechaDesdeParam, fechaHastaParam, redirectAttributes);
        }

        if (fechaDesde.isAfter(fechaHasta)) {
            return redirectConError("El rango de fechas es inválido: 'Desde' no puede ser mayor que 'Hasta'.",
                    fechaDesdeParam, fechaHastaParam, redirectAttributes);
        }

        byte[] csv = csvService.exportarPedidosCsv(fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ventas.csv")
                .body(csv);
    }

    @GetMapping(value = "/exportar-datos/movimientos.csv", produces = "text/csv")
    public Object exportarMovimientosCsv(
            @RequestParam(name = "fechaDesde", required = false) String fechaDesdeParam,
            @RequestParam(name = "fechaHasta", required = false) String fechaHastaParam,
            RedirectAttributes redirectAttributes) {

        LocalDate fechaDesde = parseDate(fechaDesdeParam);
        LocalDate fechaHasta = parseDate(fechaHastaParam);

        if (fechaDesde == null || fechaHasta == null) {
            return redirectConError("Formato de fecha inválido. Usá un formato válido de fecha.",
                    fechaDesdeParam, fechaHastaParam, redirectAttributes);
        }

        if (fechaDesde.isAfter(fechaHasta)) {
            return redirectConError("El rango de fechas es inválido: 'Desde' no puede ser mayor que 'Hasta'.",
                    fechaDesdeParam, fechaHastaParam, redirectAttributes);
        }

        byte[] csv = csvService.exportarMovimientosStockCsv(fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movimientos-stock.csv")
                .body(csv);
    }

    private String redirectConError(String message, String fechaDesdeParam, String fechaHastaParam,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
        if (fechaDesdeParam != null && !fechaDesdeParam.isBlank()) {
            redirectAttributes.addAttribute("fechaDesde", fechaDesdeParam);
        }
        if (fechaHastaParam != null && !fechaHastaParam.isBlank()) {
            redirectAttributes.addAttribute("fechaHasta", fechaHastaParam);
        }
        return "redirect:/exportar-datos";
    }

    private LocalDate parseDateOrDefault(String value, LocalDate defaultValue) {
        LocalDate parsed = parseDate(value);
        return parsed != null ? parsed : defaultValue;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
