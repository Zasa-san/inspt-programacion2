package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import inspt_programacion2_kfc.backend.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.services.users.UserService;
import inspt_programacion2_kfc.frontend.services.NavbarService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

@Controller
public class UsersPageController {

    private final NavbarService navbarService;
    private final UserService userService;

    public UsersPageController(NavbarService navbarService, UserService userService) {
        this.navbarService = navbarService;
        this.userService = userService;
    }

    @GetMapping("/users")
    public String usersPage(Model model) {
        PageMetadata page = new PageMetadata("Usuarios");
        model.addAttribute("page", page);
        model.addAttribute("navLinks", navbarService.getLinksForRoute("users"));

        var users = userService.findAll();
        List<UserResponseDTO> dtos = users.stream().map(u -> new UserResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getRole().toString(),
                u.isEnabled()
        )).collect(Collectors.toList());

        model.addAttribute("users", dtos);
        return "users";
    }
}
