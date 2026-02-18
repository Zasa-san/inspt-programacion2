package inspt_programacion2_kfc.frontend.controllers;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductImageException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductNotFoundException;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.frontend.services.FrontProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductsPageController {

    private final FrontProductoService frontProductoService;

    public ProductsPageController(FrontProductoService frontProductoService) {
        this.frontProductoService = frontProductoService;
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        PageMetadata page = new PageMetadata("Productos", "Administración de productos");
        model.addAttribute("page", page);

        List<ProductoEntity> productos = frontProductoService.findAll();
        model.addAttribute("products", productos);
        return "products/index";
    }

    @GetMapping("/products/new")
    public String newProductPage(Model model) {
        PageMetadata page = new PageMetadata("Nuevo producto");
        model.addAttribute("page", page);

        model.addAttribute("product", new ProductoEntity());
        return "products/new";
    }

    //todo traer items disponibles en NEW y EDIT para inyectar en la busqueda de items disponibles
    @PostMapping("/products/new")
    public String createProductPage(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(name = "precio") Integer precio,
            @RequestParam(name = "gruposJson") String gruposJson,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttrs) {

        try {
            frontProductoService.create(name, description, gruposJson, precio, imageFile);
            redirectAttrs.addFlashAttribute("successMessage", "Producto creado correctamente.");
        } catch (ProductImageException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/products/new";
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al crear producto: " + e.getMessage());
            return "redirect:/products/new";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error inesperado: " + e.getMessage());
            return "redirect:/products/new";
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        PageMetadata page = new PageMetadata("Editar producto");
        model.addAttribute("page", page);

        ProductoEntity producto = frontProductoService.findProductoById(id);

        if (producto == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/products";
        }

        model.addAttribute("product", producto);

        return "products/edit";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(name = "precio") Integer precio,
            @RequestParam(name = "gruposJson") String gruposJson,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) Boolean removeImage,
            RedirectAttributes redirectAttrs) {

        try {
            boolean removeImageValue = removeImage != null && removeImage;
            frontProductoService.update(id, name, description, gruposJson, precio, imageFile, removeImageValue);

            redirectAttrs.addFlashAttribute("successMessage", "Producto actualizado correctamente.");
        } catch (ProductNotFoundException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products";
        } catch (ProductImageException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar producto: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        }

        return "redirect:/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            frontProductoService.delete(id);
            redirectAttrs.addFlashAttribute("successMessage", "Producto eliminado correctamente.");
        } catch (ProductNotFoundException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/products/toggle-availability/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            frontProductoService.toggleAvailability(id);
            redirectAttrs.addFlashAttribute("successMessage", "Disponibilidad actualizada correctamente.");
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar disponibilidad: " + e.getMessage());
        }
        return "redirect:/products";
    }

}
