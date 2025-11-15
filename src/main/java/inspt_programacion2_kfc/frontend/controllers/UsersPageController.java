package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import inspt_programacion2_kfc.backend.models.dto.users.UserRequestDTO;
import inspt_programacion2_kfc.backend.models.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.models.users.Role;
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
        model.addAttribute("navLinks", navbarService.getLinksForRoute("admin"));

        var users = userService.findAll();
        List<UserResponseDTO> dtos = users.stream().map(u -> new UserResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getRole().toString(),
                u.isEnabled()
        )).collect(Collectors.toList());

        model.addAttribute("users", dtos);
        return "users/index";
    }

    @GetMapping("/users/new")
    public String newUserPage(Model model) {
        PageMetadata page = new PageMetadata("Nuevo usuario");
        model.addAttribute("page", page);
        model.addAttribute("navLinks", navbarService.getLinksForRoute("admin"));

        model.addAttribute("user", new UserRequestDTO());
        model.addAttribute("roles", Role.values());

        return "users/new";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable Long id, Model model) {
        PageMetadata page = new PageMetadata("Editar usuario");
        model.addAttribute("page", page);
        model.addAttribute("navLinks", navbarService.getLinksForRoute("admin"));

        var userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/users";
        }

        var u = userOpt.get();
        UserRequestDTO dto = new UserRequestDTO(u.getUsername(), "", u.getRole().name());
        model.addAttribute("user", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("userId", id);

        return "users/edit";
    }

    @org.springframework.web.bind.annotation.PostMapping("/users/new")
    public String createUserFromForm(
            @org.springframework.web.bind.annotation.RequestParam String username,
            @org.springframework.web.bind.annotation.RequestParam String password,
            @org.springframework.web.bind.annotation.RequestParam String role,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs
    ) {
        try {
            userService.create(username, password, Role.valueOf(role), false);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/error";
        }
        return "redirect:/users";
    }

    @org.springframework.web.bind.annotation.PostMapping("/users/edit/{id}")
    public String updateUserFromForm(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam String username,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String password,
            @org.springframework.web.bind.annotation.RequestParam String role,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            userService.update(id, username, password, Role.valueOf(role));
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/error";
        }
        return "redirect:/users";
    }

    @GetMapping("/users/error")
    public String usersErrorPage(Model model) {
        PageMetadata page = new PageMetadata("Error");
        model.addAttribute("page", page);
        model.addAttribute("navLinks", navbarService.getLinksForRoute("admin"));
        return "users/error";
    }
}
