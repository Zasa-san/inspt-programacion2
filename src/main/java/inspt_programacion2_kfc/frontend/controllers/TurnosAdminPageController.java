package inspt_programacion2_kfc.frontend.controllers;

import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.services.users.TurnoService;
import inspt_programacion2_kfc.frontend.models.Dias;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Time;

@Controller
public class TurnosAdminPageController {

    private final TurnoService turnoService;

    public TurnosAdminPageController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping("/turnos/new")
    public String newTurnoPage(@RequestParam int dia, Model model) {
        int selectedDia = clampDia(dia);

        PageMetadata page = new PageMetadata("Nuevo turno");
        model.addAttribute("page", page);
        model.addAttribute("selectedDia", selectedDia);
        model.addAttribute("selectedDiaNombre", Dias.numeroADia(selectedDia));

        return "turnos/new";
    }

    @PostMapping("/turnos/new")
    public String createTurno(
            @RequestParam int dia,
            @RequestParam String ingreso,
            @RequestParam String salida,
            RedirectAttributes redirectAttrs
    ) {
        int selectedDia = clampDia(dia);

        try {
            Time ingresoTime = parseTime(ingreso);
            Time salidaTime = parseTime(salida);

            Turno created = turnoService.create(ingresoTime, salidaTime, selectedDia);
            redirectAttrs.addFlashAttribute("successMessage", "Turno creado (ID " + created.getId() + ").");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", "Horario inválido.");
            return "redirect:/turnos/new?dia=" + selectedDia;
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("errorMessage", "No se pudo crear el turno.");
            return "redirect:/turnos/new?dia=" + selectedDia;
        }

        return "redirect:/turnos";
    }

    @PostMapping("/turnos/delete/{id}")
    public String deleteTurno(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            turnoService.delete(id);
            redirectAttrs.addFlashAttribute("successMessage", "Turno eliminado.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", "No se pudo eliminar el turno (tiene datos relacionados).");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("errorMessage", "No se pudo eliminar el turno.");
        }
        return "redirect:/turnos";
    }

    private static int clampDia(int dia) {
        if (dia < 1) {
            return 1;
        }
        if (dia > 7) {
            return 7;
        }
        return dia;
    }

    private static Time parseTime(String hhmm) {
        if (hhmm == null || hhmm.isBlank()) {
            throw new IllegalArgumentException("hora vacía");
        }
        return Time.valueOf(hhmm.trim() + ":00");
    }
}

