package inspt_programacion2_kfc.frontend.controllers;

import java.io.IOException;
import java.util.List;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.files.FileUploadService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

@Controller
public class ProductsPageController {

    private final ProductoService productoService;
    private final FileUploadService fileUploadService;

    public ProductsPageController(ProductoService productoService, FileUploadService fileUploadService) {
        this.productoService = productoService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Elimina la imagen asociada a un producto, si existe.
     */
    private void deleteProductImage(ProductoEntity producto) {
        if (producto != null && producto.getImgUrl() != null) {
            fileUploadService.deleteFile(producto.getImgUrl());
        }
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        PageMetadata page = new PageMetadata("Productos", "Administración de productos");
        model.addAttribute("page", page);

        List<ProductoEntity> productos = productoService.findAll();
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

    @PostMapping("/products/new")
    public String createProductFromForm(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int price,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttrs) {

        try {
            ProductoEntity producto = new ProductoEntity();
            producto.setName(name);
            producto.setDescription(description);
            producto.setPrice(price);

            if (imageFile != null && !imageFile.isEmpty() && imageFile.getSize() > 0) {
                String imageUrl = fileUploadService.saveFile(imageFile, "products");
                producto.setImgUrl(imageUrl);
            } else {
                producto.setImgUrl(AppConstants.DEFAULT_IMG_URL);
            }

            productoService.save(producto);
            redirectAttrs.addFlashAttribute("successMessage", "Producto creado correctamente.");
        } catch (IOException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/products/new";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al crear producto: " + e.getMessage());
            return "redirect:/products/new";
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        PageMetadata page = new PageMetadata("Editar producto");
        model.addAttribute("page", page);

        ProductoEntity producto = productoService.findById(id);
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
            @RequestParam int price,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) boolean removeImage,
            RedirectAttributes redirectAttrs) {

        try {
            ProductoEntity producto = productoService.findById(id);
            if (producto == null) {
                redirectAttrs.addFlashAttribute("errorMessage", "Producto no encontrado.");
                return "redirect:/products";
            }

            producto.setName(name);
            producto.setDescription(description);
            producto.setPrice(price);

            if (removeImage) {
                deleteProductImage(producto);
                producto.setImgUrl(null);
            }

            if (imageFile != null && !imageFile.isEmpty() && imageFile.getSize() > 0) {
                deleteProductImage(producto);
                String imageUrl = fileUploadService.saveFile(imageFile, "products");
                producto.setImgUrl(imageUrl);
            }

            productoService.update(producto);
            redirectAttrs.addFlashAttribute("successMessage", "Producto actualizado correctamente.");
        } catch (IOException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar producto: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        }

        return "redirect:/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            ProductoEntity producto = productoService.findById(id);
            if (producto != null) {
                deleteProductImage(producto);
                productoService.deleteById(id);
                redirectAttrs.addFlashAttribute("successMessage", "Producto eliminado correctamente.");
            } else {
                redirectAttrs.addFlashAttribute("errorMessage", "Producto no encontrado.");
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/products/toggle-availability/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productoService.toggleAvailability(id);
            redirectAttrs.addFlashAttribute("successMessage", "Disponibilidad actualizada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar disponibilidad: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
