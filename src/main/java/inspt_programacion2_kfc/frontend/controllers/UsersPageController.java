package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import inspt_programacion2_kfc.backend.models.dto.users.UserRequestDTO;
import inspt_programacion2_kfc.backend.models.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.users.UserService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsersPageController {

    private final UserService userService;

    public UsersPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String usersPage(Model model, Authentication authentication) {
        PageMetadata page = new PageMetadata("Usuarios");
        model.addAttribute("page", page);

        var users = userService.findAll();
        List<UserResponseDTO> dtos = users.stream().map(u -> new UserResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getRole().toString(),
                u.isEnabled()
        )).collect(Collectors.toList());

        model.addAttribute("users", dtos);

        // ID del usuario actualmente autenticado, para ocultar el botón de eliminarse a sí mismo
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("currentUserId", currentUser.getId());
        }

        return "users/index";
    }

    @GetMapping("/users/new")
    public String newUserPage(Model model) {
        PageMetadata page = new PageMetadata("Nuevo usuario");
        model.addAttribute("page", page);

        model.addAttribute("user", new UserRequestDTO());
        model.addAttribute("roles", Role.values());

        return "users/new";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable Long id, Model model, Authentication authentication) {
        PageMetadata page = new PageMetadata("Editar usuario");
        model.addAttribute("page", page);

        var userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/users";
        }

        var u = userOpt.get();
        UserRequestDTO dto = new UserRequestDTO(u.getUsername(), "", u.getRole().name());
        model.addAttribute("user", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("userId", id);

        // Indica si se está editando al mismo usuario que está logueado
        boolean editingSelf = false;
        if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
            editingSelf = currentUser.getId() != null && currentUser.getId().equals(id);
        }
        model.addAttribute("editingSelf", editingSelf);

        return "users/edit";
    }

    @PostMapping("/users/new")
    public String createUserFromForm(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role, RedirectAttributes redirectAttrs
    ) {
        try {
            userService.create(username, password, Role.valueOf(role), false);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/error";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUserFromForm(@PathVariable Long id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam String role,
            RedirectAttributes redirectAttrs,
            Authentication authentication) {
        try {
            Role newRole = Role.valueOf(role);

            // Si el usuario edita su propio perfil, no se permite cambiar el rol
            if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
                if (currentUser.getId() != null && currentUser.getId().equals(id)) {
                    var existingUserOpt = userService.findById(id);
                    if (existingUserOpt.isPresent()) {
                        newRole = existingUserOpt.get().getRole();
                    }
                }
            }

            userService.update(id, username, password, newRole);
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
        return "users/error";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttrs, Authentication authentication) {
        try {
            if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
                if (currentUser.getId() != null && currentUser.getId().equals(id)) {
                    redirectAttrs.addFlashAttribute("errorMessage",
                            "No podés eliminar tu propio usuario estando logueado.");
                    return "redirect:/users";
                }
            }

            userService.delete(id);
            redirectAttrs.addFlashAttribute("successMessage", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/error";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserEnabled(@PathVariable Long id,
                                    @RequestParam boolean enabled, RedirectAttributes redirectAttrs, Authentication authentication) {
        try {
            if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
                if (currentUser.getId() != null && currentUser.getId().equals(id)) {
                    redirectAttrs.addFlashAttribute("errorMessage",
                            "No podes deshabilitar tu propio usuario estando logueado.");
                    return "redirect:/users";
                }
            }

            userService.toggleEnabled(id, enabled);
            String status = enabled ? "habilitado" : "deshabilitado";
            redirectAttrs.addFlashAttribute("successMessage", "Usuario " + status + " correctamente");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/error";
        }
        return "redirect:/users";
    }
}
