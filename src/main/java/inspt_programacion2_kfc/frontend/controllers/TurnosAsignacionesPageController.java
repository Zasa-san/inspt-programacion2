package inspt_programacion2_kfc.frontend.controllers;

import inspt_programacion2_kfc.backend.models.users.AsignacionTurno;
import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.users.AsignacionTurnoService;
import inspt_programacion2_kfc.backend.services.users.TurnoService;
import inspt_programacion2_kfc.backend.services.users.UserService;
import inspt_programacion2_kfc.frontend.models.Dias;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class TurnosAsignacionesPageController {

    private final TurnoService turnoService;
    private final UserService userService;
    private final AsignacionTurnoService asignacionTurnoService;

    public TurnosAsignacionesPageController(
            TurnoService turnoService,
            UserService userService,
            AsignacionTurnoService asignacionTurnoService
    ) {
        this.turnoService = turnoService;
        this.userService = userService;
        this.asignacionTurnoService = asignacionTurnoService;
    }

    @GetMapping("/turnos/asignaciones")
    public String asignacionesPage(
            @RequestParam(required = false, defaultValue = "1") int dia,
            Model model
    ) {
        int selectedDia = clampDia(dia);

        PageMetadata page = new PageMetadata("Asignación de turnos");
        model.addAttribute("page", page);
        model.addAttribute("dias", Dias.values());
        model.addAttribute("selectedDia", selectedDia);
        model.addAttribute("selectedDiaNombre", Dias.numeroADia(selectedDia));

        List<Turno> turnosDelDia = turnoService.findByDiaSorted(selectedDia);
        model.addAttribute("turnosDelDia", turnosDelDia);

        List<AsignacionTurno> asignacionesDelDia = asignacionTurnoService.findVigentesByDia(selectedDia);
        Map<Long, List<AsignacionTurno>> asignacionesPorTurno = asignacionesDelDia.stream()
                .collect(Collectors.groupingBy(a -> a.getTurno().getId()));
        model.addAttribute("asignacionesPorTurno", asignacionesPorTurno);

        List<User> usuariosHabilitados = userService.findAll().stream()
                .filter(User::isEnabled)
                .sorted(Comparator.comparing(User::getApellido)
                        .thenComparing(User::getNombre)
                        .thenComparing(User::getUsername))
                .toList();

        Map<Long, Set<Long>> userIdsAsignadosPorTurno = asignacionesDelDia.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getTurno().getId(),
                        Collectors.mapping(a -> a.getUsuario().getId(), Collectors.toSet())
                ));

        Map<Long, List<User>> usuariosDisponiblesPorTurno = turnosDelDia.stream()
                .collect(Collectors.toMap(
                        Turno::getId,
                        turno -> {
                            Set<Long> assigned = userIdsAsignadosPorTurno.get(turno.getId());
                            if (assigned == null || assigned.isEmpty()) {
                                return usuariosHabilitados;
                            }
                            return usuariosHabilitados.stream()
                                    .filter(u -> !assigned.contains(u.getId()))
                                    .toList();
                        }
                ));

        model.addAttribute("usuariosDisponiblesPorTurno", usuariosDisponiblesPorTurno);

        return "turnos/asignaciones";
    }

    @PostMapping("/turnos/asignaciones/asignar")
    public String asignar(
            @RequestParam int dia,
            @RequestParam Long turnoId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttrs
    ) {
        int selectedDia = clampDia(dia);

        Turno turno = turnoService.findById(turnoId);
        if (turno == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Turno no encontrado.");
            return "redirect:/turnos/asignaciones?dia=" + selectedDia;
        }

        User usuario = userService.findById(userId);
        if (usuario == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/turnos/asignaciones?dia=" + turno.getDia();
        }

        try {
            asignacionTurnoService.asignarTurnoVigente(usuario, turno);
            redirectAttrs.addFlashAttribute("successMessage", "Usuario asignado correctamente.");
        } catch (IllegalStateException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("errorMessage", "No se pudo asignar el turno.");
        }

        return "redirect:/turnos/asignaciones?dia=" + turno.getDia();
    }

    @PostMapping("/turnos/asignaciones/quitar/{asignacionId}")
    public String quitar(
            @PathVariable Long asignacionId,
            @RequestParam int dia,
            RedirectAttributes redirectAttrs
    ) {
        int selectedDia = clampDia(dia);

        try {
            asignacionTurnoService.eliminar(asignacionId);
            redirectAttrs.addFlashAttribute("successMessage", "Asignación eliminada.");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("errorMessage", "No se pudo eliminar la asignación.");
        }

        return "redirect:/turnos/asignaciones?dia=" + selectedDia;
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
}
