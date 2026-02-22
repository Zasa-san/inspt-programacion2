package inspt_programacion2_kfc.frontend.controllers;

import inspt_programacion2_kfc.backend.models.users.Turno;
import inspt_programacion2_kfc.backend.services.users.TurnoService;
import inspt_programacion2_kfc.frontend.models.Dias;
import inspt_programacion2_kfc.frontend.models.turnos.TurnoDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TurnosPageController {

    private final TurnoService turnoService;

    public TurnosPageController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }
//accion de solo asignar para admin

    @GetMapping("/turnos")
    public String turnosPage(Model model, Authentication authentication) {
        PageMetadata page = new PageMetadata("Turnos");
        model.addAttribute("page", page);

        List<Turno> turnos = turnoService.findAllSorted();

        List<TurnoDTO> turnoDTOS = turnos.stream()
                .map(turno -> new TurnoDTO(
                        turno.getId(),
                        turno.getIngreso(),
                        turno.getSalida(),
                        turno.getDia()
                ))
                .toList();

        Map<Integer, List<TurnoDTO>> turnosPorDia = Arrays.stream(Dias.values())
                .collect(Collectors.toMap(
                        Dias::getValor,
                        d -> new ArrayList<>(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        for (TurnoDTO dto : turnoDTOS) {
            turnosPorDia.computeIfAbsent(dto.getDia(), k -> new ArrayList<>()).add(dto);
        }

        model.addAttribute("turnosPorDia", turnosPorDia);

        return "turnos/index";
    }

}
