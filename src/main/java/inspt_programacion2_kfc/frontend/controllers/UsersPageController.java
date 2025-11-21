package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import inspt_programacion2_kfc.backend.exceptions.user.UserAlreadyExistsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.dto.users.UserRequestDTO;
import inspt_programacion2_kfc.backend.models.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.users.UserService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

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

        boolean isAdmin = false;
        Long currentUserId;

        if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
            isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
            currentUserId = currentUser.getId();
            model.addAttribute("currentUserId", currentUserId);
        } else {
            currentUserId = null;
        }

        List<User> users = userService.findAll();

        List<User> filteredUsers;
        if (isAdmin) {
            filteredUsers = users;
        } else {
            filteredUsers = users.stream()
                    .filter(u -> u.getId().equals(currentUserId))
                    .collect(Collectors.toList());
        }

        List<UserResponseDTO> dtos = filteredUsers.stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getRole().getRoleName(), user.isEnabled()))
                .collect(Collectors.toList());

        model.addAttribute("users", dtos);
        model.addAttribute("isAdmin", isAdmin);

        return "users/index";
    }

    @GetMapping("/users/new")
    public String newUserPage(Model model, Authentication authentication) {
        // Solo admin puede crear usuarios
        if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
            if (currentUser.getRole() != Role.ROLE_ADMIN) {
                return "redirect:/access-denied";
            }
        }

        PageMetadata page = new PageMetadata("Nuevo usuario");
        model.addAttribute("page", page);

        model.addAttribute("user", new UserRequestDTO());
        model.addAttribute("roles", Role.values());

        return "users/new";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = null;
        boolean isAdmin = false;

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
            isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        }

        // No-admin solo puede editar su propio usuario
        if (!isAdmin && (currentUser == null || !currentUser.getId().equals(id))) {
            return "redirect:/access-denied";
        }

        PageMetadata page = new PageMetadata("Editar usuario");
        model.addAttribute("page", page);

        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/users";
        }

        UserRequestDTO dto = new UserRequestDTO(user.getUsername(), "", user.getRole().name());
        model.addAttribute("user", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("userId", id);

        // Indica si se está editando al mismo usuario que está logueado
        boolean editingSelf = currentUser.getId() != null && currentUser.getId().equals(id);
        model.addAttribute("editingSelf", editingSelf);
        model.addAttribute("isAdmin", isAdmin);

        return "users/edit";
    }

    @PostMapping("/users/new")
    public String createUserFromForm(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            RedirectAttributes redirectAttrs,
            Authentication authentication
    ) {
        // Solo admin puede crear usuarios
        if (authentication != null && authentication.getPrincipal() instanceof User currentUser) {
            if (currentUser.getRole() != Role.ROLE_ADMIN) {
                redirectAttrs.addFlashAttribute("errorMessage", "No tienes permiso para crear usuarios.");
                return "redirect:/access-denied";
            }
        }

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
            User currentUser = null;
            boolean isAdmin = false;

            if (authentication != null && authentication.getPrincipal() instanceof User) {
                currentUser = (User) authentication.getPrincipal();
                isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
            }

            if (userService.existsByUsername(username)) {
                throw new UserAlreadyExistsException("Ya existe un usuario con el nombre ingresado, intente con otro.");
            }

            // No-admin solo puede editar su propio usuario
            if (!isAdmin && (currentUser == null || !currentUser.getId().equals(id))) {
                redirectAttrs.addFlashAttribute("errorMessage", "No tienes permiso para editar este usuario.");
                return "redirect:/access-denied";
            }

            Role newRole = Role.valueOf(role);

            // Si el usuario edita su propio perfil, no se permite cambiar el rol
            if (currentUser.getId() != null && currentUser.getId().equals(id)) {
                User existingUser = userService.findById(id);
                if (existingUser != null) {
                    newRole = existingUser.getRole();
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
            User currentUser = null;
            boolean isAdmin = false;

            if (authentication != null && authentication.getPrincipal() instanceof User) {
                currentUser = (User) authentication.getPrincipal();
                isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
            }

            // Solo admin puede eliminar usuarios
            if (!isAdmin) {
                redirectAttrs.addFlashAttribute("errorMessage", "No tienes permiso para eliminar usuarios.");
                return "redirect:/access-denied";
            }

            // Admin no puede auto-eliminarse
            if (currentUser.getId() != null && currentUser.getId().equals(id)) {
                redirectAttrs.addFlashAttribute("errorMessage",
                        "No podés eliminar tu propio usuario estando logueado.");
                return "redirect:/users";
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
            User currentUser = null;
            boolean isAdmin = false;

            if (authentication != null && authentication.getPrincipal() instanceof User) {
                currentUser = (User) authentication.getPrincipal();
                isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
            }

            // Solo admin puede cambiar el estado de usuarios
            if (!isAdmin) {
                redirectAttrs.addFlashAttribute("errorMessage", "No tienes permiso para cambiar el estado de usuarios.");
                return "redirect:/access-denied";
            }

            // Admin no puede auto-desactivarse
            if (!enabled && currentUser.getId() != null && currentUser.getId().equals(id)) {
                redirectAttrs.addFlashAttribute("errorMessage",
                        "No podes deshabilitar tu propio usuario estando logueado.");
                return "redirect:/users";
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
