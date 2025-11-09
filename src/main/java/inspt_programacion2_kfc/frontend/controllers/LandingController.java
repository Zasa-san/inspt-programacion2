package inspt_programacion2_kfc.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("mensaje", "Hola Mundo!");
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

}
