package inspt_programacion2_kfc.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

@Controller
public class ProductsPageController {

    private final ProductoService productoService;

    public ProductsPageController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        PageMetadata page = new PageMetadata("Productos", "Administración de productos");
        model.addAttribute("page", page);

        var productos = productoService.findAll();
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
            @RequestParam(required = false) String imgUrl,
            RedirectAttributes redirectAttrs) {

        try {
            ProductoEntity p = new ProductoEntity();
            p.setName(name);
            p.setDescription(description);
            p.setPrice(price);
            p.setImgUrl(imgUrl);
            productoService.save(p);
            redirectAttrs.addFlashAttribute("successMessage", "Producto creado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al crear producto: " + e.getMessage());
            return "redirect:/products/new";
        }

        return "redirect:/products";
    }
}
