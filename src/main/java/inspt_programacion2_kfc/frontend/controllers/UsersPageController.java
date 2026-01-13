package inspt_programacion2_kfc.frontend.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.exceptions.user.UserAlreadyExistsException;
import inspt_programacion2_kfc.backend.exceptions.user.UserException;
import inspt_programacion2_kfc.backend.models.dto.users.UserRequestDTO;
import inspt_programacion2_kfc.backend.models.dto.users.UserResponseDTO;
import inspt_programacion2_kfc.backend.models.users.Role;
import inspt_programacion2_kfc.backend.models.users.User;
import inspt_programacion2_kfc.backend.services.users.UserService;

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
                .map(user -> new UserResponseDTO(user))
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
        User currentUser = (User) authentication.getPrincipal();
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/users";
        }

        PageMetadata page = new PageMetadata("Editar usuario");
        model.addAttribute("page", page);

        UserRequestDTO dto = new UserRequestDTO(user.getUsername(), "", user.getDni(), user.getNombre(), user.getApellido(), user.getRole().name());
        dto.setEnabled(user.isEnabled());
        model.addAttribute("user", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("userId", id);

        // Indica si el admin está editando su propio perfil
        boolean editingSelf = currentUser.getId().equals(id);
        model.addAttribute("editingSelf", editingSelf);

        return "users/edit";
    }

    @GetMapping("/users/change-password")
    public String changePasswordPage(Model model, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/login";
        }

        PageMetadata page = new PageMetadata("Cambiar Contraseña");
        model.addAttribute("page", page);

        return "users/change-password";
    }

    @PostMapping("/users/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttrs,
            Authentication authentication
    ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User currentUser)) {
                return "redirect:/login";
            }

            // Validar que las contraseñas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttrs.addFlashAttribute("errorMessage", "Las contraseñas no coinciden.");
                return "redirect:/users/change-password";
            }

            // Verificar contraseña actual y cambiar
            boolean changed = userService.changePassword(currentUser.getId(), currentPassword, newPassword);

            if (changed) {
                redirectAttrs.addFlashAttribute("successMessage", "Contraseña cambiada correctamente.");
                return "redirect:/users";
            } else {
                redirectAttrs.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
                return "redirect:/users/change-password";
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/change-password";
        }
    }

    @GetMapping("/users/change-user-password/{id}")
    public String changeUserPasswordPage(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/login";
        }

        User targetUser = userService.findById(id);
        if (targetUser == null) {
            return "redirect:/users";
        }

        PageMetadata page = new PageMetadata("Cambiar Contraseña de Usuario");
        model.addAttribute("page", page);
        model.addAttribute("targetUser", targetUser);

        return "users/change-user-password";
    }

    @PostMapping("/users/change-user-password/{id}")
    public String changeUserPassword(
            @PathVariable Long id,
            @RequestParam String adminPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttrs,
            Authentication authentication
    ) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User currentUser)) {
                return "redirect:/login";
            }

            User targetUser = userService.findById(id);
            if (targetUser == null) {
                redirectAttrs.addFlashAttribute("errorMessage", "Usuario no encontrado.");
                return "redirect:/users";
            }

            // Validar que las contraseñas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttrs.addFlashAttribute("errorMessage", "Las contraseñas no coinciden.");
                return "redirect:/users/change-user-password/" + id;
            }

            // Verificar contraseña del admin
            if (!userService.verifyPassword(currentUser.getId(), adminPassword)) {
                redirectAttrs.addFlashAttribute("errorMessage", "Tu contraseña es incorrecta.");
                return "redirect:/users/change-user-password/" + id;
            }

            // Cambiar la contraseña del usuario objetivo
            userService.changePasswordByAdmin(id, newPassword);
            redirectAttrs.addFlashAttribute("successMessage", "Contraseña del usuario cambiada correctamente.");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/change-user-password/" + id;
        }
    }

    @PostMapping("/users/new")
    public String createUserFromForm(
            UserRequestDTO dto,
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

            userService.create(dto.getUsername(), dto.getPassword(), dto.getDni(),
                    dto.getNombre(), dto.getApellido(), Role.valueOf(dto.getRole()), false);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/error";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUserFromForm(@PathVariable Long id,
            UserRequestDTO dto,
            RedirectAttributes redirectAttrs,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            User existingUser = userService.findById(id);

            if (existingUser != null && !existingUser.getUsername().equals(dto.getUsername())
                    && userService.existsByUsername(dto.getUsername())) {
                throw new UserAlreadyExistsException(String.format("El nombre de usuario %s ya existe.", dto.getUsername()));
            }

            if (existingUser != null && existingUser.getDni() != dto.getDni()
                    && userService.existsByDni(dto.getDni())) {
                throw new UserAlreadyExistsException(String.format("El DNI %d ya está asignado a otro usuario.", dto.getDni()));
            }

            Role newRole = Role.valueOf(dto.getRole());

            // Admin no puede cambiar su propio rol
            if (currentUser.getId().equals(id) && existingUser != null) {
                newRole = existingUser.getRole();
            }

            // Valor por defecto de enabled si no viene en el formulario (checkbox no marcado)
            boolean enabledValue = dto.getEnabled() != null && dto.getEnabled();

            // Admin no puede auto-deshabilitarse
            if (currentUser.getId().equals(id)) {
                enabledValue = true;
            }

            userService.update(id, dto.getUsername(), dto.getDni(),
                    dto.getNombre(), dto.getApellido(), newRole, enabledValue);
        } catch (UserException e) {
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
            if (currentUser.getId().equals(id)) {
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
            if (!enabled && currentUser.getId().equals(id)) {
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
