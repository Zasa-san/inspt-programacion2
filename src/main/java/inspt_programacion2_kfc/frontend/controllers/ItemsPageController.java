package inspt_programacion2_kfc.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.stock.Item;
import inspt_programacion2_kfc.backend.services.stock.ItemService;

@Controller
public class ItemsPageController {

    private final ItemService itemService;

    public ItemsPageController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public String itemsPage(Model model) {
        PageMetadata page = new PageMetadata("Items", "Gestión de items de stock");
        model.addAttribute("page", page);
        model.addAttribute("items", itemService.findAll());
        return "items/index";
    }

    @GetMapping("/items/nuevo")
    public String nuevoItemPage(Model model) {
        PageMetadata page = new PageMetadata("Nuevo item", "Crear un nuevo item de stock");
        model.addAttribute("page", page);
        return "items/nuevo";
    }

    @PostMapping("/items/nuevo")
    public String crearNuevoItem(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") int precio,
            RedirectAttributes redirectAttrs) {

        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttrs.addFlashAttribute("errorMessage", "El nombre del item es requerido.");
                return "redirect:/items/nuevo";
            }

            if (precio < 0) {
                redirectAttrs.addFlashAttribute("errorMessage", "El precio no puede ser negativo.");
                return "redirect:/items/nuevo";
            }

            itemService.create(nombre, descripcion, precio);
            redirectAttrs.addFlashAttribute("successMessage", "Item creado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/items/nuevo";
        }

        return "redirect:/items";
    }

    @GetMapping("/items/editar/{id}")
    public String editarItemPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        PageMetadata page = new PageMetadata("Editar item", "Modificar un item de stock");
        model.addAttribute("page", page);

        Item item = itemService.findById(id);
        if (item == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Item no encontrado.");
            return "redirect:/items";
        }

        model.addAttribute("item", item);
        return "items/editar";
    }

    @PostMapping("/items/editar/{id}")
    public String actualizarItem(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") int precio,
            RedirectAttributes redirectAttrs) {

        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttrs.addFlashAttribute("errorMessage", "El nombre del item es requerido.");
                return "redirect:/items/editar/" + id;
            }

            if (precio < 0) {
                redirectAttrs.addFlashAttribute("errorMessage", "El precio no puede ser negativo.");
                return "redirect:/items/editar/" + id;
            }

            itemService.update(id, nombre, descripcion, precio);
            redirectAttrs.addFlashAttribute("successMessage", "Item actualizado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/items/editar/" + id;
        }

        return "redirect:/items";
    }

    @PostMapping("/items/eliminar/{id}")
    public String eliminarItem(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            itemService.delete(id);
            redirectAttrs.addFlashAttribute("successMessage", "Item eliminado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/items";
    }
}
