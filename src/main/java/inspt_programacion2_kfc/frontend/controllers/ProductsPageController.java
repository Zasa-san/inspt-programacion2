package inspt_programacion2_kfc.frontend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductImageException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductNotFoundException;
import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.bundles.ProductoComponenteService;
import inspt_programacion2_kfc.backend.services.ingredients.IngredienteService;
import inspt_programacion2_kfc.backend.services.ingredients.ProductoIngredienteService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.helpers.ProductHelper;
import inspt_programacion2_kfc.frontend.models.Customizacion;

@Controller
public class ProductsPageController {

    private final ProductoService productoService;
    private final IngredienteService ingredienteService;
    private final ProductoIngredienteService productoIngredienteService;
    private final ProductoComponenteService productoComponenteService;
    private final ProductHelper productHelper;

    public ProductsPageController(
            ProductoService productoService,
            IngredienteService ingredienteService,
            ProductoIngredienteService productoIngredienteService,
            ProductoComponenteService productoComponenteService,
            ProductHelper productHelper) {
        this.productoService = productoService;
        this.ingredienteService = ingredienteService;
        this.productoIngredienteService = productoIngredienteService;
        this.productoComponenteService = productoComponenteService;
        this.productHelper = productHelper;
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
        model.addAttribute("ingredientes", ingredienteService.findAllActive());
        model.addAttribute("recetaMap", new HashMap<Long, Integer>());
        model.addAttribute("productosBundle", productoService.findAll());
        model.addAttribute("bundleMap", new HashMap<Long, Integer>());
        return "products/new";
    }

    @PostMapping("/products/new")
    public String createProductFromForm(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int price,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) String customizationsJson,
            @RequestParam(name = "ingredienteId", required = false) List<Long> ingredienteIds,
            @RequestParam(name = "ingredienteCantidad", required = false) List<Integer> ingredienteCantidades,
            @RequestParam(name = "componenteId", required = false) List<Long> componenteIds,
            @RequestParam(name = "componenteCantidad", required = false) List<Integer> componenteCantidades,
            RedirectAttributes redirectAttrs) {

        try {
            boolean tieneReceta = tieneAlgunaCantidad(ingredienteCantidades);
            boolean tieneBundle = tieneAlgunaCantidad(componenteCantidades);
            if (tieneReceta == tieneBundle) {
                redirectAttrs.addFlashAttribute("errorMessage",
                        "Definí receta (ingredientes) o bundle (componentes), pero no ambos (y al menos uno).");
                return "redirect:/products/new";
            }

            ProductoEntity producto = new ProductoEntity();
            producto.setName(name);
            producto.setDescription(description);
            producto.setPrice(price);

            producto = productoService.createAndReturn(producto, imageFile);

            if (tieneReceta) {
                productoComponenteService.clearComponentes(producto.getId());
                productoIngredienteService.setReceta(producto, ingredienteIds, ingredienteCantidades);
            } else {
                productoIngredienteService.clearReceta(producto.getId());
                productoComponenteService.setComponentes(producto, componenteIds, componenteCantidades);
            }

            List<Customizacion> customizations = productHelper.parseCustomizations(customizationsJson);
            productHelper.handleCustomizations(producto, customizations);

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

        ProductoEntity producto = productoService.findById(id);
        if (producto == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/products";
        }

        List<CustomizacionEntity> customizaciones = productHelper.getCustomizacionesPorProducto(producto);

        model.addAttribute("product", producto);
        model.addAttribute("customizaciones", customizaciones);
        model.addAttribute("ingredientes", ingredienteService.findAll());
        model.addAttribute("recetaMap", productoIngredienteService.obtenerRecetaMap(producto.getId()));
        model.addAttribute("productosBundle",
                productoService.findAll().stream().filter(p -> !p.getId().equals(producto.getId())).toList());
        model.addAttribute("bundleMap", productoComponenteService.obtenerComponentesMap(producto.getId()));

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
            @RequestParam(required = false) String customizationsJson,
            @RequestParam(name = "ingredienteId", required = false) List<Long> ingredienteIds,
            @RequestParam(name = "ingredienteCantidad", required = false) List<Integer> ingredienteCantidades,
            @RequestParam(name = "componenteId", required = false) List<Long> componenteIds,
            @RequestParam(name = "componenteCantidad", required = false) List<Integer> componenteCantidades,
            RedirectAttributes redirectAttrs) {

        try {
            boolean tieneReceta = tieneAlgunaCantidad(ingredienteCantidades);
            boolean tieneBundle = tieneAlgunaCantidad(componenteCantidades);
            if (tieneReceta == tieneBundle) {
                redirectAttrs.addFlashAttribute("errorMessage",
                        "Definí receta (ingredientes) o bundle (componentes), pero no ambos (y al menos uno).");
                return "redirect:/products/edit/" + id;
            }

            ProductoEntity updatedData = new ProductoEntity();
            updatedData.setName(name);
            updatedData.setDescription(description);
            updatedData.setPrice(price);

            productoService.update(id, updatedData, imageFile, removeImage);

            ProductoEntity producto = productoService.findById(id);
            if (tieneReceta) {
                productoComponenteService.clearComponentes(producto.getId());
                productoIngredienteService.setReceta(producto, ingredienteIds, ingredienteCantidades);
            } else {
                productoIngredienteService.clearReceta(producto.getId());
                productoComponenteService.setComponentes(producto, componenteIds, componenteCantidades);
            }
            List<Customizacion> customizations = productHelper.parseCustomizations(customizationsJson);
            productHelper.handleCustomizations(producto, customizations);

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

    private boolean tieneAlgunaCantidad(List<Integer> cantidades) {
        if (cantidades == null) {
            return false;
        }
        for (Integer c : cantidades) {
            if (c != null && c > 0) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productoService.delete(id);
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
            productoService.toggleAvailability(id);
            redirectAttrs.addFlashAttribute("successMessage", "Disponibilidad actualizada correctamente.");
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar disponibilidad: " + e.getMessage());
        }
        return "redirect:/products";
    }

}
