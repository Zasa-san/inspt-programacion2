package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductImageException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductNotFoundException;
import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.CustomizacionesService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.controllers.dto.CustomizationDto;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

@Controller
public class ProductsPageController {

    private final ProductoService productoService;
    private final CustomizacionesService customizacionesService;
    private final ObjectMapper objectMapper;

    public ProductsPageController(ProductoService productoService, CustomizacionesService customizacionesService, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.customizacionesService = customizacionesService;
        this.objectMapper = objectMapper;
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
            @RequestParam(required = false) String customizationsJson,
            RedirectAttributes redirectAttrs) {

        try {
            ProductoEntity producto = new ProductoEntity();
            producto.setName(name);
            producto.setDescription(description);
            producto.setPrice(price);

            productoService.create(producto, imageFile);

            List<CustomizationDto> customizations = parseCustomizations(customizationsJson);
            handleCustomizations(producto, customizations);

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

        List<CustomizacionEntity> customizaciones = customizacionesService.findByProducto(producto);

        model.addAttribute("product", producto);
        model.addAttribute("customizaciones", customizaciones);

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
            RedirectAttributes redirectAttrs) {

        try {
            ProductoEntity updatedData = new ProductoEntity();
            updatedData.setName(name);
            updatedData.setDescription(description);
            updatedData.setPrice(price);

            productoService.update(id, updatedData, imageFile, removeImage);

            ProductoEntity producto = productoService.findById(id);
            List<CustomizationDto> customizations = parseCustomizations(customizationsJson);
            handleCustomizations(producto, customizations);

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

    private void handleCustomizations(ProductoEntity producto, List<CustomizationDto> customizations) {
        if (customizations == null || customizations.isEmpty()) {
            return;
        }
        for (CustomizationDto dto : customizations) {
            String idStr = dto.getId();
            String nombre = dto.getNombre();
            // Asegurar que el precio nunca sea negativo
            int priceModifier = Math.max(0, Objects.requireNonNullElse(dto.getPriceModifier(), 0));
            boolean enabled = Objects.requireNonNullElse(dto.getEnabled(), false);

            if (StringUtils.isNumeric(idStr)) {
                Long customizationId = Long.valueOf(idStr);
                if (!enabled) {
                    customizacionesService.delete(customizationId);
                } else {
                    CustomizacionEntity existing = customizacionesService.findById(customizationId);
                    if (existing != null) {
                        existing.setNombre(nombre);
                        existing.setPriceModifier(priceModifier);
                        customizacionesService.update(customizationId, existing);
                    }
                }
            } else if (idStr != null && idStr.startsWith("NEW_") && enabled && nombre != null && !nombre.trim().isEmpty()) {

                CustomizacionEntity newCustomization = new CustomizacionEntity();
                newCustomization.setProducto(producto);
                newCustomization.setNombre(nombre);
                newCustomization.setPriceModifier(priceModifier);
                customizacionesService.create(newCustomization);
            }

        }
    }

    private List<CustomizationDto> parseCustomizations(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CustomizationDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al parsear customizaciones JSON", e);
        }
    }
}
