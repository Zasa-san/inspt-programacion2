package inspt_programacion2_kfc.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import inspt_programacion2_kfc.frontend.utils.PageMetadata;

@Controller
public class RouteController {

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        PageMetadata page = new PageMetadata("Inicio", "Página pública de acceso al sistema de gestión de KFC");
        model.addAttribute("page", page);
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        PageMetadata page = new PageMetadata("Iniciar sesión");
        model.addAttribute("page", page);
        return "login";
    }

}
